package co.grap.pack.grap.user.content.service;

import co.grap.pack.common.util.PaginationUtil;
import co.grap.pack.grap.realestate.support.RealEstateDisplayUtils;
import co.grap.pack.grap.realestate.support.RealEstateMortgageCalculator;
import co.grap.pack.grap.user.content.mapper.CmsUserRealEstateMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자 부동산 화면 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CmsUserRealEstateService {

    private static final int DEFAULT_PAGE_SIZE = 12;

    private final CmsUserRealEstateMapper realEstateMapper;
    private final ObjectMapper objectMapper;

    public Map<String, Object> getRealEstateList(String keyword, String dealYearMonth, String sort, Integer page) {
        String latestMonth = realEstateMapper.selectLatestDealYearMonth();
        String normalizedMonth = hasText(dealYearMonth) ? dealYearMonth : latestMonth;
        List<Map<String, String>> availableMonths = toMonthOptions(realEstateMapper.selectAvailableDealYearMonths());
        int totalCount = hasText(normalizedMonth)
                ? realEstateMapper.selectRealEstateCount(keyword, normalizedMonth)
                : 0;

        Map<String, Object> paginationResult = PaginationUtil.createPaginationResult(totalCount, page, DEFAULT_PAGE_SIZE);
        int offset = (int) paginationResult.get("offset");
        int size = (int) paginationResult.get("size");

        List<Map<String, Object>> propertyList = hasText(normalizedMonth)
                ? realEstateMapper.selectRealEstateList(keyword, normalizedMonth, normalizeSort(sort), offset, size)
                : new ArrayList<>();
        propertyList.forEach(this::decorateProperty);

        Map<String, Object> result = new HashMap<>();
        result.putAll(paginationResult);
        result.put("propertyList", propertyList);
        result.put("currentPropertyMonth", normalizedMonth);
        result.put("formattedCurrentPropertyMonth", RealEstateDisplayUtils.formatYearMonthLabel(normalizedMonth));
        result.put("availableMonths", availableMonths);
        result.put("filters", Map.of(
                "keyword", defaultString(keyword),
                "dealYearMonth", defaultString(normalizedMonth),
                "sort", normalizeSort(sort)
        ));
        result.put("sortOptions", createSortOptions());
        result.put("hasData", hasText(latestMonth));

        return result;
    }

    public Map<String, Object> getRealEstateDetail(Long id, Integer requestedYear) {
        Map<String, Object> property = realEstateMapper.selectRealEstateById(id);
        if (property == null) {
            return null;
        }

        decorateProperty(property);

        String propertyMatchKey = defaultString(property.get("propertyMatchKey"));
        List<Integer> availableYears = realEstateMapper.selectPropertyAvailableYears(propertyMatchKey);
        int selectedYear = resolveSelectedYear(requestedYear, availableYears);
        String metricType = "rent".equals(property.get("transactionType")) ? "rent" : "trade";
        List<Map<String, Object>> priceSeries = selectedYear > 0
                ? buildPriceSeries(realEstateMapper.selectPropertyMonthlyAverageHistory(propertyMatchKey, selectedYear, metricType))
                : List.of();

        Map<String, Object> defaults = new LinkedHashMap<>(RealEstateMortgageCalculator.createDefaultFormValues());
        Map<String, Object> calculatorPolicy = new LinkedHashMap<>();
        calculatorPolicy.put("buyerProfiles", RealEstateMortgageCalculator.BUYER_PROFILES);
        calculatorPolicy.put("areaPolicies", RealEstateMortgageCalculator.AREA_POLICIES);

        Map<String, Object> result = new HashMap<>();
        result.put("property", property);
        result.put("availableYears", availableYears);
        result.put("selectedYear", selectedYear);
        result.put("priceSeries", priceSeries);
        result.put("priceSeriesJson", toJson(priceSeries));
        result.put("chartMetricLabel", "rent".equals(metricType) ? "월별 평균 보증금" : "월별 평균 거래가");
        result.put("mortgageDefaults", defaults);
        result.put("calculatorPolicy", calculatorPolicy);

        if (Boolean.TRUE.equals(property.get("mortgageSupported"))) {
            result.put("mortgageScenario", buildMortgageScenarioView(property, defaults));
        }

        return result;
    }

    private List<Map<String, Object>> buildPriceSeries(List<Map<String, Object>> historyRows) {
        List<Map<String, Object>> priceSeries = new ArrayList<>();
        for (Map<String, Object> historyRow : historyRows) {
            Map<String, Object> point = new LinkedHashMap<>();
            int dealMonth = safeInt(historyRow.get("dealMonth"));
            int averageAmount = safeInt(historyRow.get("averageAmount"));
            point.put("label", dealMonth + "월");
            point.put("value", averageAmount);
            priceSeries.add(point);
        }
        return priceSeries;
    }

    private Map<String, Object> buildMortgageScenarioView(Map<String, Object> property, Map<String, Object> defaults) {
        RealEstateMortgageCalculator.MortgageInput input = RealEstateMortgageCalculator.MortgageInput.builder()
                .propertyPrice(safeInt(property.get("tradeAmountManwon")))
                .cashAmount(safeInt(defaults.get("cashAmount")))
                .annualIncome(safeInt(defaults.get("annualIncome")))
                .existingMonthlyDebtPayment(safeInt(defaults.get("existingMonthlyDebtPayment")))
                .interestRate(safeDouble(defaults.get("interestRate")))
                .loanTermYears(safeInt(defaults.get("loanTermYears")))
                .dsrLimitRatio(safeDouble(defaults.get("dsrLimitRatio")))
                .buyerProfile(defaultString(defaults.get("buyerProfile")))
                .areaPolicy(defaultString(defaults.get("areaPolicy")))
                .stressRate(RealEstateMortgageCalculator.getDefaultStressRate(defaultString(defaults.get("areaPolicy"))))
                .build();

        RealEstateMortgageCalculator.MortgageScenario scenario = RealEstateMortgageCalculator.calculateScenario(input);

        Map<String, Object> view = new LinkedHashMap<>();
        view.put("requiredLoanAmount", scenario.getRequiredLoanAmount());
        view.put("maxAvailableLoan", scenario.getMaxAvailableLoan());
        view.put("additionalCashNeeded", scenario.getAdditionalCashNeeded());
        view.put("monthlyPaymentForMaxLoan", Math.round(scenario.getMonthlyPaymentForMaxLoan()));
        view.put("applicableLtvRatio", scenario.getApplicableLtvRatio() * 100);
        view.put("requiredLoanToValueRatio", scenario.getRequiredLoanToValueRatio());
        view.put("availableLoanToValueRatio", scenario.getAvailableLoanToValueRatio());
        view.put("maxByLtv", scenario.getMaxByLtv());
        view.put("maxByDsr", scenario.getMaxByDsr());
        view.put("purchaseLoanCapAmount", scenario.getPurchaseLoanCapAmount());
        view.put("stressRate", scenario.getStressRate());
        view.put("monthlyRepaymentBudget", Math.round(scenario.getMonthlyRepaymentBudget()));
        view.put("monthlyPaymentForRequiredLoan", Math.round(scenario.getMonthlyPaymentForRequiredLoan()));
        view.put("affordable", scenario.isAffordable());
        view.put("constraint", scenario.getConstraint());
        view.put("formattedRequiredLoanAmount", RealEstateDisplayUtils.formatPriceLabel(scenario.getRequiredLoanAmount()));
        view.put("formattedMaxAvailableLoan", RealEstateDisplayUtils.formatPriceLabel(scenario.getMaxAvailableLoan()));
        view.put("formattedAdditionalCashNeeded", RealEstateDisplayUtils.formatPriceLabel(scenario.getAdditionalCashNeeded()));
        view.put("formattedMonthlyPaymentForMaxLoan", RealEstateDisplayUtils.formatPriceLabel(Math.round(scenario.getMonthlyPaymentForMaxLoan())));
        view.put("formattedApplicableLtvRatio", RealEstateDisplayUtils.formatPercent(scenario.getApplicableLtvRatio() * 100));
        view.put("formattedRequiredLoanToValueRatio", RealEstateDisplayUtils.formatPercent(scenario.getRequiredLoanToValueRatio()));
        view.put("formattedAvailableLoanToValueRatio", RealEstateDisplayUtils.formatPercent(scenario.getAvailableLoanToValueRatio()));
        view.put("formattedMaxByLtv", RealEstateDisplayUtils.formatPriceLabel(scenario.getMaxByLtv()));
        view.put("formattedMaxByDsr", RealEstateDisplayUtils.formatPriceLabel(scenario.getMaxByDsr()));
        view.put("formattedStressRate", RealEstateDisplayUtils.formatPercent(scenario.getStressRate()));
        view.put("formattedMonthlyRepaymentBudget", RealEstateDisplayUtils.formatPriceLabel(Math.round(scenario.getMonthlyRepaymentBudget())));
        view.put("formattedMonthlyPaymentForRequiredLoan", RealEstateDisplayUtils.formatPriceLabel(Math.round(scenario.getMonthlyPaymentForRequiredLoan())));
        view.put("statusTitle", buildStatusTitle(scenario));
        view.put("statusMessage", buildStatusMessage(scenario));
        view.put("constraintLabel", buildConstraintLabel(scenario.getConstraint()));
        view.put("policyCapLabel", Double.isFinite(scenario.getPurchaseLoanCapAmount())
                ? RealEstateDisplayUtils.formatPriceLabel((int) Math.round(scenario.getPurchaseLoanCapAmount()))
                : "상한 없음");
        view.put("buyerProfileLabel", findLabel(RealEstateMortgageCalculator.BUYER_PROFILES, input.getBuyerProfile()));
        view.put("areaPolicyLabel", findLabel(RealEstateMortgageCalculator.AREA_POLICIES, input.getAreaPolicy()));
        return view;
    }

    private void decorateProperty(Map<String, Object> property) {
        String propertyCategory = defaultString(property.get("propertyCategory"));
        String transactionType = defaultString(property.get("transactionType"));
        int tradeAmount = safeInt(property.get("tradeAmountManwon"));
        int depositAmount = safeInt(property.get("depositAmountManwon"));
        int monthlyRent = safeInt(property.get("monthlyRentManwon"));
        int displayAmount = "rent".equals(transactionType) ? depositAmount : tradeAmount;

        property.put("categoryLabel", RealEstateDisplayUtils.getCategoryLabel(propertyCategory));
        property.put("transactionLabel", RealEstateDisplayUtils.getTransactionLabel(transactionType));
        property.put("amountLabel", RealEstateDisplayUtils.getAmountLabel(transactionType));
        property.put("formattedDisplayAmount", RealEstateDisplayUtils.formatPriceLabel(displayAmount));
        property.put("formattedMonthlyRent", RealEstateDisplayUtils.formatMonthlyRentLabel(monthlyRent));
        property.put("formattedDealDate", RealEstateDisplayUtils.formatDealDateLabel(toLocalDate(property.get("dealDate"))));
        property.put("formattedDealYearMonth", RealEstateDisplayUtils.formatYearMonthLabel(defaultString(property.get("dealYearMonth"))));
        property.put("formattedAreaLabel", RealEstateDisplayUtils.formatAreaLabel(toBigDecimal(property.get("areaM2"))));
        property.put("formattedFloor", RealEstateDisplayUtils.formatFloorLabel(safeInt(property.get("floor"))));
        property.put("formattedBuildYear", RealEstateDisplayUtils.formatBuildYearLabel(safeInt(property.get("buildYear"))));
        property.put("mortgageSupported", RealEstateMortgageCalculator.isSupportedProperty(propertyCategory, transactionType));
    }

    private List<Map<String, String>> toMonthOptions(List<String> yearMonths) {
        return yearMonths.stream()
                .map(value -> Map.of(
                        "value", value,
                        "label", RealEstateDisplayUtils.formatYearMonthLabel(value)
                ))
                .toList();
    }

    private List<Map<String, String>> createSortOptions() {
        return List.of(
                Map.of("value", "latest", "label", "최신 계약일순"),
                Map.of("value", "amountDesc", "label", "금액 높은순"),
                Map.of("value", "amountAsc", "label", "금액 낮은순"),
                Map.of("value", "nameAsc", "label", "이름순")
        );
    }

    private String normalizeSort(String sort) {
        if ("amountDesc".equals(sort) || "amountAsc".equals(sort) || "nameAsc".equals(sort)) {
            return sort;
        }
        return "latest";
    }

    private int resolveSelectedYear(Integer requestedYear, List<Integer> availableYears) {
        if (availableYears == null || availableYears.isEmpty()) {
            return requestedYear == null ? 0 : requestedYear;
        }
        if (requestedYear != null && availableYears.contains(requestedYear)) {
            return requestedYear;
        }
        return availableYears.get(0);
    }

    private String buildStatusTitle(RealEstateMortgageCalculator.MortgageScenario scenario) {
        if (scenario.getRequiredLoanAmount() <= 0) {
            return "현재 현금만으로도 매수가 가능합니다.";
        }
        if (scenario.isAffordable()) {
            return "현재 조건으로 매수 가능성이 있습니다.";
        }
        return "추가 현금이 더 필요합니다.";
    }

    private String buildStatusMessage(RealEstateMortgageCalculator.MortgageScenario scenario) {
        if (scenario.getRequiredLoanAmount() <= 0) {
            return "보유 현금이 매매가 이상이라 대출 없이도 매수 가능한 상태입니다.";
        }
        if (scenario.isAffordable()) {
            return "LTV, DSR, 정책 상한을 반영한 현재 계산상 매수가 가능합니다.";
        }
        return "현재 조건에서는 추가 현금 " + RealEstateDisplayUtils.formatPriceLabel(scenario.getAdditionalCashNeeded()) + " 정도가 더 필요합니다.";
    }

    private String buildConstraintLabel(String constraint) {
        if ("additionalPurchaseRestricted".equals(constraint)) {
            return "규제지역 추가 매수 제한";
        }
        if ("ltv".equals(constraint)) {
            return "LTV 한도";
        }
        if ("policyCap".equals(constraint)) {
            return "정책 대출 상한";
        }
        if ("dsr".equals(constraint)) {
            return "DSR 한도";
        }
        return "제한 없음";
    }

    private String findLabel(List<Map<String, String>> options, String value) {
        return options.stream()
                .filter(option -> value.equals(option.get("value")))
                .map(option -> option.get("label"))
                .findFirst()
                .orElse("");
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("부동산 화면 JSON을 만들지 못했습니다.", exception);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String defaultString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private int safeInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        String normalized = value.toString().replaceAll("[^\\d-]", "");
        if (normalized.isBlank()) {
            return 0;
        }
        return Integer.parseInt(normalized);
    }

    private double safeDouble(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        String normalized = value.toString().replaceAll("[^\\d.-]", "");
        if (normalized.isBlank()) {
            return 0;
        }
        return Double.parseDouble(normalized);
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        String normalized = value.toString().replaceAll("[^\\d.-]", "");
        if (normalized.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(normalized);
    }

    private LocalDate toLocalDate(Object value) {
        if (value instanceof LocalDate localDate) {
            return localDate;
        }
        if (value instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return null;
    }
}
