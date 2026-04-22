package co.grap.pack.grap.realestate.support;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * 국토교통부 실거래 데이터셋 정의.
 */
@Getter
public enum RealEstateDataset {

    APT_TRADE(
            "apt-trade",
            "/RTMSDataSvcAptTrade/getRTMSDataSvcAptTrade",
            "apartment",
            "아파트",
            "trade",
            "매매",
            "거래가",
            "201512",
            item -> value(item, "aptNm"),
            item -> value(item, "excluUseAr"),
            item -> value(item, "buildYear"),
            item -> value(item, "floor"),
            item -> value(item, "dealAmount"),
            item -> "0",
            item -> List.of(
                    value(item, "aptNm"),
                    value(item, "jibun"),
                    value(item, "excluUseAr"),
                    value(item, "floor"),
                    value(item, "buildYear")
            )
    ),
    ROWHOUSE_TRADE(
            "rowhouse-trade",
            "/RTMSDataSvcRHTrade/getRTMSDataSvcRHTrade",
            "rowhouse",
            "연립다세대",
            "trade",
            "매매",
            "거래가",
            "201512",
            item -> {
                String name = value(item, "mhouseNm");
                return name.isBlank() ? value(item, "houseType") : name;
            },
            item -> value(item, "excluUseAr"),
            item -> value(item, "buildYear"),
            item -> value(item, "floor"),
            item -> value(item, "dealAmount"),
            item -> "0",
            item -> List.of(
                    value(item, "mhouseNm"),
                    value(item, "houseType"),
                    value(item, "jibun"),
                    value(item, "excluUseAr"),
                    value(item, "floor")
            )
    ),
    ROWHOUSE_RENT(
            "rowhouse-rent",
            "/RTMSDataSvcRHRent/getRTMSDataSvcRHRent",
            "rowhouse",
            "연립다세대",
            "rent",
            "전월세",
            "보증금",
            "201512",
            item -> {
                String name = value(item, "mhouseNm");
                return name.isBlank() ? value(item, "houseType") : name;
            },
            item -> value(item, "excluUseAr"),
            item -> value(item, "buildYear"),
            item -> value(item, "floor"),
            item -> value(item, "deposit"),
            item -> value(item, "monthlyRent"),
            item -> List.of(
                    value(item, "mhouseNm"),
                    value(item, "houseType"),
                    value(item, "jibun"),
                    value(item, "excluUseAr"),
                    value(item, "floor")
            )
    ),
    OFFICETEL_TRADE(
            "officetel-trade",
            "/RTMSDataSvcOffiTrade/getRTMSDataSvcOffiTrade",
            "officetel",
            "오피스텔",
            "trade",
            "매매",
            "거래가",
            "201512",
            item -> value(item, "offiNm"),
            item -> value(item, "excluUseAr"),
            item -> value(item, "buildYear"),
            item -> value(item, "floor"),
            item -> value(item, "dealAmount"),
            item -> "0",
            item -> List.of(
                    value(item, "offiNm"),
                    value(item, "jibun"),
                    value(item, "excluUseAr"),
                    value(item, "floor"),
                    value(item, "buildYear")
            )
    ),
    OFFICETEL_RENT(
            "officetel-rent",
            "/RTMSDataSvcOffiRent/getRTMSDataSvcOffiRent",
            "officetel",
            "오피스텔",
            "rent",
            "전월세",
            "보증금",
            "201512",
            item -> value(item, "offiNm"),
            item -> value(item, "excluUseAr"),
            item -> value(item, "buildYear"),
            item -> value(item, "floor"),
            item -> value(item, "deposit"),
            item -> value(item, "monthlyRent"),
            item -> List.of(
                    value(item, "offiNm"),
                    value(item, "jibun"),
                    value(item, "excluUseAr"),
                    value(item, "floor"),
                    value(item, "buildYear")
            )
    ),
    DETACHED_TRADE(
            "detached-trade",
            "/RTMSDataSvcSHTrade/getRTMSDataSvcSHTrade",
            "detached-house",
            "단독/다가구",
            "trade",
            "매매",
            "거래가",
            "201512",
            item -> {
                String houseType = value(item, "houseType");
                return houseType.isBlank() ? "단독/다가구" : "단독/다가구(" + houseType + ")";
            },
            item -> value(item, "totalFloorAr"),
            item -> value(item, "buildYear"),
            item -> "0",
            item -> value(item, "dealAmount"),
            item -> "0",
            item -> List.of(
                    value(item, "houseType"),
                    value(item, "jibun"),
                    value(item, "totalFloorAr"),
                    value(item, "buildYear")
            )
    ),
    DETACHED_RENT(
            "detached-rent",
            "/RTMSDataSvcSHRent/getRTMSDataSvcSHRent",
            "detached-house",
            "단독/다가구",
            "rent",
            "전월세",
            "보증금",
            "201512",
            item -> {
                String houseType = value(item, "houseType");
                return houseType.isBlank() ? "단독/다가구" : "단독/다가구(" + houseType + ")";
            },
            item -> value(item, "totalFloorAr"),
            item -> value(item, "buildYear"),
            item -> "0",
            item -> value(item, "deposit"),
            item -> value(item, "monthlyRent"),
            item -> List.of(
                    value(item, "houseType"),
                    value(item, "jibun"),
                    value(item, "totalFloorAr"),
                    value(item, "buildYear")
            )
    ),
    COMMERCIAL_TRADE(
            "commercial-trade",
            "/RTMSDataSvcNrgTrade/getRTMSDataSvcNrgTrade",
            "commercial",
            "상업/업무용",
            "trade",
            "매매",
            "거래가",
            "201512",
            item -> {
                String name = value(item, "buildingUse");
                return name.isBlank() ? value(item, "buildingType") : name;
            },
            item -> value(item, "buildingAr"),
            item -> value(item, "buildYear"),
            item -> value(item, "floor"),
            item -> value(item, "dealAmount"),
            item -> "0",
            item -> List.of(
                    value(item, "buildingUse"),
                    value(item, "buildingType"),
                    value(item, "jibun"),
                    value(item, "buildingAr"),
                    value(item, "floor")
            )
    );

    private final String datasetId;
    private final String endpoint;
    private final String propertyCategory;
    private final String categoryLabel;
    private final String transactionType;
    private final String transactionLabel;
    private final String amountLabel;
    private final String startYearMonth;
    private final Function<Map<String, String>, String> nameExtractor;
    private final Function<Map<String, String>, String> areaExtractor;
    private final Function<Map<String, String>, String> buildYearExtractor;
    private final Function<Map<String, String>, String> floorExtractor;
    private final Function<Map<String, String>, String> primaryAmountExtractor;
    private final Function<Map<String, String>, String> secondaryAmountExtractor;
    private final Function<Map<String, String>, List<String>> matchPartExtractor;

    RealEstateDataset(
            String datasetId,
            String endpoint,
            String propertyCategory,
            String categoryLabel,
            String transactionType,
            String transactionLabel,
            String amountLabel,
            String startYearMonth,
            Function<Map<String, String>, String> nameExtractor,
            Function<Map<String, String>, String> areaExtractor,
            Function<Map<String, String>, String> buildYearExtractor,
            Function<Map<String, String>, String> floorExtractor,
            Function<Map<String, String>, String> primaryAmountExtractor,
            Function<Map<String, String>, String> secondaryAmountExtractor,
            Function<Map<String, String>, List<String>> matchPartExtractor
    ) {
        this.datasetId = datasetId;
        this.endpoint = endpoint;
        this.propertyCategory = propertyCategory;
        this.categoryLabel = categoryLabel;
        this.transactionType = transactionType;
        this.transactionLabel = transactionLabel;
        this.amountLabel = amountLabel;
        this.startYearMonth = startYearMonth;
        this.nameExtractor = nameExtractor;
        this.areaExtractor = areaExtractor;
        this.buildYearExtractor = buildYearExtractor;
        this.floorExtractor = floorExtractor;
        this.primaryAmountExtractor = primaryAmountExtractor;
        this.secondaryAmountExtractor = secondaryAmountExtractor;
        this.matchPartExtractor = matchPartExtractor;
    }

    public String getRawName(Map<String, String> item) {
        return nameExtractor.apply(item);
    }

    public String getAreaValue(Map<String, String> item) {
        return areaExtractor.apply(item);
    }

    public String getBuildYearValue(Map<String, String> item) {
        return buildYearExtractor.apply(item);
    }

    public String getFloorValue(Map<String, String> item) {
        return floorExtractor.apply(item);
    }

    public String getPrimaryAmountValue(Map<String, String> item) {
        return primaryAmountExtractor.apply(item);
    }

    public String getSecondaryAmountValue(Map<String, String> item) {
        return secondaryAmountExtractor.apply(item);
    }

    public List<String> getMatchParts(Map<String, String> item) {
        return matchPartExtractor.apply(item);
    }

    public String getReadableCategoryLabel(Map<String, String> item) {
        if (!"detached-house".equals(propertyCategory)) {
            return categoryLabel;
        }

        String houseType = value(item, "houseType").replace(" ", "");
        if ("단독".equals(houseType)) {
            return "단독주택";
        }
        if ("다가구".equals(houseType)) {
            return "다가구주택";
        }
        if (!houseType.isBlank()) {
            return houseType + " 주택";
        }
        return "주택";
    }

    public static RealEstateDataset fromDatasetId(String datasetId) {
        return Arrays.stream(values())
                .filter(candidate -> candidate.datasetId.equals(datasetId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 데이터셋입니다: " + datasetId));
    }

    public static String normalizeKeyText(String value) {
        return value(value)
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }

    private static String value(Map<String, String> item, String key) {
        String value = item.get(key);
        return value == null ? "" : value.trim();
    }

    private static String value(String value) {
        return value == null ? "" : value.trim();
    }
}
