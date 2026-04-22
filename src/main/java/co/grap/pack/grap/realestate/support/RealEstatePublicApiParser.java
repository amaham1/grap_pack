package co.grap.pack.grap.realestate.support;

import co.grap.pack.grap.realestate.model.RealEstateTransactionRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 부동산 공공데이터 XML 파서 및 정규화기.
 */
@Component
@RequiredArgsConstructor
public class RealEstatePublicApiParser {

    private static final BigDecimal PYEONG_DIVISOR = new BigDecimal("3.305785");
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("^(?:\\(|\\[)?\\d+(?:-\\d+)?(?:\\)|\\])?$");

    private final ObjectMapper objectMapper;

    public ParsedPage parse(String xmlBody, RealEstateDataset dataset, String lawdCode, String defaultSggName, LocalDateTime fetchedAt) {
        Document document = parseDocument(xmlBody);
        String resultCode = text(document, "resultCode");
        String resultMessage = text(document, "resultMsg");
        if (!resultCode.isBlank() && !resultCode.matches("^0+$")) {
            throw new IllegalStateException(resultMessage.isBlank() ? "공공데이터 API 요청에 실패했습니다." : resultMessage);
        }

        int totalCount = toInteger(text(document, "totalCount"));
        NodeList itemNodes = document.getElementsByTagName("item");
        List<RealEstateTransactionRecord> items = new ArrayList<>();
        for (int index = 0; index < itemNodes.getLength(); index += 1) {
            items.add(normalizeItem((Element) itemNodes.item(index), dataset, lawdCode, defaultSggName, fetchedAt));
        }
        return new ParsedPage(totalCount, items);
    }

    private Document parseDocument(String xmlBody) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlBody)));
        } catch (Exception exception) {
            throw new IllegalStateException("공공데이터 XML 응답을 해석하지 못했습니다.", exception);
        }
    }

    private RealEstateTransactionRecord normalizeItem(
            Element itemElement,
            RealEstateDataset dataset,
            String lawdCode,
            String defaultSggName,
            LocalDateTime fetchedAt
    ) {
        Map<String, String> rawItem = mapItem(itemElement);
        String sggName = value(rawItem.get("sggNm"));
        if (sggName.isBlank()) {
            sggName = defaultSggName;
        }
        String umdName = value(rawItem.get("umdNm"));
        String jibun = value(rawItem.get("jibun"));
        BigDecimal areaM2 = toDecimal(dataset.getAreaValue(rawItem));
        Integer buildYear = toInteger(dataset.getBuildYearValue(rawItem));
        Integer floor = toInteger(dataset.getFloorValue(rawItem));
        LocalDate dealDate = toDealDate(rawItem);
        String dealYearMonth = dealDate != null
                ? String.format(Locale.ROOT, "%04d%02d", dealDate.getYear(), dealDate.getMonthValue())
                : "";
        ResolvedName resolvedName = resolvePropertyName(dataset, rawItem, sggName, umdName, jibun);
        String address = String.join(" ",
                filterBlanks(List.of("제주특별자치도", sggName, umdName, jibun))
        );
        String propertyMatchKey = buildPropertyMatchKey(dataset, lawdCode, rawItem, areaM2, floor, buildYear);
        Integer primaryAmount = toInteger(dataset.getPrimaryAmountValue(rawItem));
        Integer secondaryAmount = toInteger(dataset.getSecondaryAmountValue(rawItem));
        String rawJson = toJson(rawItem);
        String externalRowKey = buildExternalRowKey(dataset.getDatasetId(), lawdCode, dealDate, rawJson);

        return RealEstateTransactionRecord.builder()
                .externalRowKey(externalRowKey)
                .propertyMatchKey(propertyMatchKey)
                .datasetId(dataset.getDatasetId())
                .propertyCategory(dataset.getPropertyCategory())
                .transactionType(dataset.getTransactionType())
                .lawdCode(lawdCode)
                .sggName(sggName)
                .umdName(umdName)
                .jibun(jibun)
                .address(address)
                .displayName(resolvedName.value())
                .nameSource(resolvedName.source())
                .areaM2(scale(areaM2))
                .areaPyeong(scale(toPyeong(areaM2)))
                .floor(floor)
                .buildYear(buildYear)
                .dealDate(dealDate)
                .dealYearMonth(dealYearMonth)
                .tradeAmountManwon("trade".equals(dataset.getTransactionType()) ? primaryAmount : 0)
                .depositAmountManwon("rent".equals(dataset.getTransactionType()) ? primaryAmount : 0)
                .monthlyRentManwon("rent".equals(dataset.getTransactionType()) ? secondaryAmount : 0)
                .latitude(null)
                .longitude(null)
                .rawJson(rawJson)
                .fetchedAt(fetchedAt)
                .updatedAt(fetchedAt)
                .build();
    }

    private Map<String, String> mapItem(Element itemElement) {
        Map<String, String> record = new LinkedHashMap<>();
        NodeList children = itemElement.getChildNodes();
        for (int index = 0; index < children.getLength(); index += 1) {
            if (children.item(index) instanceof Element childElement) {
                record.put(childElement.getNodeName(), value(childElement.getTextContent()));
            }
        }
        return record;
    }

    private ResolvedName resolvePropertyName(RealEstateDataset dataset, Map<String, String> rawItem, String sggName, String umdName, String jibun) {
        String rawName = value(dataset.getRawName(rawItem));
        if (!isPlaceholderPropertyName(rawName, jibun) && !isGenericCategoryName(rawName, dataset)) {
            return new ResolvedName(rawName, "original");
        }
        return new ResolvedName(createFallbackPropertyName(dataset, rawItem, sggName, umdName, jibun), "fallback");
    }

    private boolean isPlaceholderPropertyName(String name, String jibun) {
        String normalizedName = value(name).replace(" ", "");
        String normalizedJibun = value(jibun).replace(" ", "");
        if (normalizedName.isBlank()) {
            return true;
        }
        if (!normalizedJibun.isBlank() && normalizedName.equals(normalizedJibun)) {
            return true;
        }
        return PLACEHOLDER_PATTERN.matcher(normalizedName).matches();
    }

    private boolean isGenericCategoryName(String name, RealEstateDataset dataset) {
        String compactName = value(name).replace(" ", "");
        String compactCategory = dataset.getCategoryLabel().replace(" ", "");
        if (compactName.isBlank()) {
            return true;
        }
        if (compactName.equals(compactCategory)) {
            return true;
        }
        return compactName.startsWith(compactCategory + "(") && compactName.endsWith(")");
    }

    private String createFallbackPropertyName(RealEstateDataset dataset, Map<String, String> rawItem, String sggName, String umdName, String jibun) {
        String readableCategory = dataset.getReadableCategoryLabel(rawItem);
        if (!umdName.isBlank()) {
            return umdName + " " + readableCategory;
        }
        if (!sggName.isBlank()) {
            return sggName + " " + readableCategory;
        }
        if (!jibun.isBlank()) {
            return readableCategory + " " + jibun;
        }
        return readableCategory;
    }

    private String buildPropertyMatchKey(
            RealEstateDataset dataset,
            String lawdCode,
            Map<String, String> rawItem,
            BigDecimal areaM2,
            Integer floor,
            Integer buildYear
    ) {
        List<String> parts = new ArrayList<>();
        parts.add(dataset.getDatasetId());
        parts.add(lawdCode);
        parts.addAll(dataset.getMatchParts(rawItem));
        parts.add(areaM2 == null ? "" : scale(areaM2).stripTrailingZeros().toPlainString());
        parts.add(floor == null ? "" : String.valueOf(floor));
        parts.add(buildYear == null ? "" : String.valueOf(buildYear));

        return parts.stream()
                .map(RealEstateDataset::normalizeKeyText)
                .reduce((left, right) -> left + "|" + right)
                .orElse("");
    }

    private String buildExternalRowKey(String datasetId, String lawdCode, LocalDate dealDate, String rawJson) {
        String dateText = dealDate == null ? "nodate" : dealDate.toString();
        return datasetId + "|" + lawdCode + "|" + dateText + "|" + sha256(rawJson);
    }

    private BigDecimal toPyeong(BigDecimal areaM2) {
        if (areaM2 == null || areaM2.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return areaM2.divide(PYEONG_DIVISOR, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal scale(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private String toJson(Map<String, String> rawItem) {
        try {
            return objectMapper.writeValueAsString(rawItem);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("실거래 원본 데이터를 JSON으로 저장하지 못했습니다.", exception);
        }
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte current : hash) {
                builder.append(String.format("%02x", current));
            }
            return builder.toString();
        } catch (Exception exception) {
            throw new IllegalStateException("실거래 데이터 해시를 만들지 못했습니다.", exception);
        }
    }

    private String text(Document document, String tagName) {
        NodeList nodes = document.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            return "";
        }
        return value(nodes.item(0).getTextContent());
    }

    private LocalDate toDealDate(Map<String, String> rawItem) {
        int year = toInteger(rawItem.get("dealYear"));
        int month = toInteger(rawItem.get("dealMonth"));
        int day = toInteger(rawItem.get("dealDay"));
        if (year <= 0 || month <= 0 || day <= 0) {
            return null;
        }
        return LocalDate.of(year, month, day);
    }

    private Integer toInteger(String value) {
        String normalized = value(value).replaceAll("[^\\d-]", "");
        if (normalized.isBlank()) {
            return 0;
        }
        return Integer.parseInt(normalized);
    }

    private BigDecimal toDecimal(String value) {
        String normalized = value(value).replaceAll("[^\\d.-]", "");
        if (normalized.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(normalized);
    }

    private List<String> filterBlanks(List<String> values) {
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .toList();
    }

    private String value(String value) {
        return value == null ? "" : value.trim();
    }

    public record ParsedPage(int totalCount, List<RealEstateTransactionRecord> items) {
    }

    private record ResolvedName(String value, String source) {
    }
}
