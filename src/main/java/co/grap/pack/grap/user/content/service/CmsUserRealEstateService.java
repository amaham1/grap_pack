package co.grap.pack.grap.user.content.service;

import co.grap.pack.common.util.PaginationUtil;
import co.grap.pack.grap.realestate.support.RealEstateDisplayUtils;
import co.grap.pack.grap.realestate.support.RealEstateMortgageCalculator;
import co.grap.pack.grap.realestate.support.RealEstatePurchaseCostCalculator;
import co.grap.pack.grap.user.content.mapper.CmsUserRealEstateMapper;
import co.grap.pack.grap.user.content.model.CmsUserRealEstateSearchParam;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 사용자 부동산 화면 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CmsUserRealEstateService {

    private static final int DEFAULT_PAGE_SIZE = 12;
    private static final int COMPARISON_LIMIT = 8;
    private static final String KAKAO_MAP_SEARCH_BASE_URL = "https://map.kakao.com/link/search/";
    private static final Set<String> PROPERTY_CATEGORIES = Set.of(
            "apartment",
            "rowhouse",
            "officetel",
            "detached-house",
            "commercial"
    );
    private static final Set<String> TRANSACTION_TYPES = Set.of("trade", "rent");

    private final CmsUserRealEstateMapper realEstateMapper;
    private final ObjectMapper objectMapper;

    public Map<String, Object> getRealEstateList(String keyword, String dealYearMonth, String sort, Integer page) {
        CmsUserRealEstateSearchParam searchParam = CmsUserRealEstateSearchParam.builder()
                .keyword(keyword)
                .dealYearMonth(dealYearMonth)
                .sort(sort)
                .page(page)
                .build();
        return getRealEstateList(searchParam);
    }

    public Map<String, Object> getRealEstateList(CmsUserRealEstateSearchParam searchParam) {
        String latestMonth = realEstateMapper.selectLatestDealYearMonth();
        CmsUserRealEstateSearchParam normalizedParam = normalizeSearchParam(searchParam, latestMonth);
        String normalizedMonth = normalizedParam.getDealYearMonth();
        List<Map<String, String>> availableMonths = toMonthOptions(realEstateMapper.selectAvailableDealYearMonths());
        List<Map<String, String>> sggOptions = hasText(normalizedMonth)
                ? toStringOptions(realEstateMapper.selectAvailableSggNames(normalizedMonth))
                : List.of();
        List<Map<String, String>> umdOptions = hasText(normalizedMonth)
                ? toStringOptions(realEstateMapper.selectAvailableUmdNames(normalizedMonth, normalizedParam.getSggName()))
                : List.of();
        int totalCount = hasText(normalizedMonth)
                ? realEstateMapper.selectRealEstateCount(normalizedParam)
                : 0;

        Map<String, Object> paginationResult = PaginationUtil.createPaginationResult(totalCount, normalizedParam.getPage(), DEFAULT_PAGE_SIZE);
        int offset = (int) paginationResult.get("offset");
        int size = (int) paginationResult.get("size");

        List<Map<String, Object>> propertyList = hasText(normalizedMonth)
                ? realEstateMapper.selectRealEstateList(normalizedParam, offset, size)
                : new ArrayList<>();
        propertyList.forEach(this::decorateProperty);

        Map<String, Object> result = new HashMap<>();
        result.putAll(paginationResult);
        result.put("propertyList", propertyList);
        result.put("currentPropertyMonth", normalizedMonth);
        result.put("formattedCurrentPropertyMonth", RealEstateDisplayUtils.formatYearMonthLabel(normalizedMonth));
        result.put("availableMonths", availableMonths);
        result.put("sggOptions", sggOptions);
        result.put("umdOptions", umdOptions);
        result.put("categoryOptions", createCategoryOptions());
        result.put("transactionOptions", createTransactionOptions());
        result.put("filters", createFilterMap(normalizedParam));
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
        Map<String, Object> trendSummary = buildTrendSummary(priceSeries);
        List<Map<String, Object>> samePropertyTransactions = hasText(propertyMatchKey)
                ? defaultList(realEstateMapper.selectSamePropertyRecentTransactions(propertyMatchKey, id, COMPARISON_LIMIT))
                : List.of();
        samePropertyTransactions.forEach(this::decorateProperty);
        List<Map<String, Object>> similarConditionTransactions = defaultList(realEstateMapper.selectSimilarConditionTransactions(property, COMPARISON_LIMIT));
        similarConditionTransactions.forEach(this::decorateProperty);

        Map<String, Object> defaults = new LinkedHashMap<>(RealEstateMortgageCalculator.createDefaultFormValues());
        Map<String, Object> calculatorPolicy = new LinkedHashMap<>();
        calculatorPolicy.put("buyerProfiles", RealEstateMortgageCalculator.BUYER_PROFILES);
        calculatorPolicy.put("areaPolicies", RealEstateMortgageCalculator.AREA_POLICIES);
        Map<String, Object> purchaseCostDefaults = new LinkedHashMap<>(RealEstatePurchaseCostCalculator.createDefaultFormValues());
        Map<String, Object> purchaseCostPolicy = new LinkedHashMap<>();
        purchaseCostPolicy.put("references", RealEstatePurchaseCostCalculator.POLICY_REFERENCES);
        purchaseCostPolicy.put("notice", "취득세 본세와 중개보수 상한을 단순 추정한 참고용 계산입니다.");

        Map<String, Object> result = new HashMap<>();
        result.put("property", property);
        result.put("availableYears", availableYears);
        result.put("selectedYear", selectedYear);
        result.put("priceSeries", priceSeries);
        result.put("priceSeriesJson", toJson(priceSeries));
        result.put("trendSummary", trendSummary);
        result.put("samePropertyTransactions", samePropertyTransactions);
        result.put("similarConditionTransactions", similarConditionTransactions);
        result.put("chartMetricLabel", "rent".equals(metricType) ? "월별 평균 보증금" : "월별 평균 거래가");
        result.put("mortgageDefaults", defaults);
        result.put("calculatorPolicy", calculatorPolicy);
        result.put("purchaseCostDefaults", purchaseCostDefaults);
        result.put("purchaseCostPolicy", purchaseCostPolicy);

        if (Boolean.TRUE.equals(property.get("mortgageSupported"))) {
            result.put("mortgageScenario", buildMortgageScenarioView(property, defaults));
        }
        if (Boolean.TRUE.equals(property.get("purchaseCostSupported"))) {
            result.put("purchaseCostScenario", buildPurchaseCostScenarioView(property, purchaseCostDefaults));
        }

        return result;
    }

    private List<Map<String, Object>> buildPriceSeries(List<Map<String, Object>> historyRows) {
        List<Map<String, Object>> priceSeries = new ArrayList<>();
        for (Map<String, Object> historyRow : historyRows) {
            Map<String, Object> point = new LinkedHashMap<>();
            int dealMonth = safeInt(historyRow.get("dealMonth"));
            int averageAmount = safeInt(historyRow.get("averageAmount"));
            int minAmount = safeInt(historyRow.get("minAmount"));
            int maxAmount = safeInt(historyRow.get("maxAmount"));
            int transactionCount = safeInt(historyRow.get("transactionCount"));
            point.put("label", dealMonth + "월");
            point.put("value", averageAmount);
            point.put("averageAmount", averageAmount);
            point.put("minAmount", minAmount);
            point.put("maxAmount", maxAmount);
            point.put("transactionCount", transactionCount);
            priceSeries.add(point);
        }
        return priceSeries;
    }

    private Map<String, Object> buildTrendSummary(List<Map<String, Object>> priceSeries) {
        Map<String, Object> summary = new LinkedHashMap<>();
        if (priceSeries == null || priceSeries.isEmpty()) {
            summary.put("hasTrend", false);
            summary.put("formattedAverageAmount", "-");
            summary.put("formattedLatestAverageAmount", "-");
            summary.put("formattedChangeAmount", "-");
            summary.put("formattedChangeRate", "-");
            summary.put("formattedTransactionCount", "0");
            summary.put("formattedMinAmount", "-");
            summary.put("formattedMaxAmount", "-");
            summary.put("latestMonthLabel", "-");
            summary.put("changeDirection", "flat");
            return summary;
        }

        int totalCount = 0;
        long weightedTotal = 0;
        int minAmount = Integer.MAX_VALUE;
        int maxAmount = 0;
        for (Map<String, Object> point : priceSeries) {
            int count = Math.max(1, safeInt(point.get("transactionCount")));
            int averageAmount = safeInt(point.get("averageAmount"));
            int monthlyMinAmount = safeInt(point.get("minAmount"));
            int monthlyMaxAmount = safeInt(point.get("maxAmount"));
            totalCount += count;
            weightedTotal += (long) averageAmount * count;
            if (monthlyMinAmount > 0) {
                minAmount = Math.min(minAmount, monthlyMinAmount);
            }
            maxAmount = Math.max(maxAmount, monthlyMaxAmount);
        }

        Map<String, Object> latestPoint = priceSeries.get(priceSeries.size() - 1);
        Map<String, Object> previousPoint = priceSeries.size() > 1 ? priceSeries.get(priceSeries.size() - 2) : null;
        int latestAverageAmount = safeInt(latestPoint.get("averageAmount"));
        int previousAverageAmount = previousPoint == null ? latestAverageAmount : safeInt(previousPoint.get("averageAmount"));
        int changeAmount = latestAverageAmount - previousAverageAmount;
        double changeRate = previousAverageAmount > 0 ? (changeAmount * 100.0) / previousAverageAmount : 0;
        int averageAmount = totalCount > 0 ? (int) Math.round((double) weightedTotal / totalCount) : 0;

        summary.put("hasTrend", true);
        summary.put("formattedAverageAmount", RealEstateDisplayUtils.formatPriceLabel(averageAmount));
        summary.put("formattedLatestAverageAmount", RealEstateDisplayUtils.formatPriceLabel(latestAverageAmount));
        summary.put("formattedChangeAmount", formatSignedPriceLabel(changeAmount));
        summary.put("formattedChangeRate", formatSignedPercent(changeRate));
        summary.put("formattedTransactionCount", String.format("%,d", totalCount));
        summary.put("formattedMinAmount", minAmount == Integer.MAX_VALUE ? "-" : RealEstateDisplayUtils.formatPriceLabel(minAmount));
        summary.put("formattedMaxAmount", maxAmount <= 0 ? "-" : RealEstateDisplayUtils.formatPriceLabel(maxAmount));
        summary.put("latestMonthLabel", defaultString(latestPoint.get("label")));
        summary.put("changeDirection", changeAmount > 0 ? "up" : changeAmount < 0 ? "down" : "flat");
        return summary;
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

    private Map<String, Object> buildPurchaseCostScenarioView(Map<String, Object> property, Map<String, Object> defaults) {
        RealEstatePurchaseCostCalculator.PurchaseCostInput input = RealEstatePurchaseCostCalculator.PurchaseCostInput.builder()
                .propertyPrice(safeInt(property.get("tradeAmountManwon")))
                .legalServiceAmount(safeInt(defaults.get("legalServiceAmount")))
                .otherCostAmount(safeInt(defaults.get("otherCostAmount")))
                .build();

        RealEstatePurchaseCostCalculator.PurchaseCostScenario scenario = RealEstatePurchaseCostCalculator.calculateScenario(input);

        Map<String, Object> view = new LinkedHashMap<>();
        view.put("propertyPrice", scenario.getPropertyPrice());
        view.put("acquisitionTaxAmount", scenario.getAcquisitionTaxAmount());
        view.put("brokerageFeeAmount", scenario.getBrokerageFeeAmount());
        view.put("legalServiceAmount", scenario.getLegalServiceAmount());
        view.put("otherCostAmount", scenario.getOtherCostAmount());
        view.put("totalExtraCostAmount", scenario.getTotalExtraCostAmount());
        view.put("totalRequiredCashAmount", scenario.getTotalRequiredCashAmount());
        view.put("acquisitionTaxRate", scenario.getAcquisitionTaxRate() * 100);
        view.put("brokerageFeeRate", scenario.getBrokerageFeeRate() * 100);
        view.put("formattedPropertyPrice", RealEstateDisplayUtils.formatPriceLabel(scenario.getPropertyPrice()));
        view.put("formattedAcquisitionTaxAmount", RealEstateDisplayUtils.formatPriceLabel(scenario.getAcquisitionTaxAmount()));
        view.put("formattedBrokerageFeeAmount", RealEstateDisplayUtils.formatPriceLabel(scenario.getBrokerageFeeAmount()));
        view.put("formattedLegalServiceAmount", RealEstateDisplayUtils.formatPriceLabel(scenario.getLegalServiceAmount()));
        view.put("formattedOtherCostAmount", RealEstateDisplayUtils.formatPriceLabel(scenario.getOtherCostAmount()));
        view.put("formattedTotalExtraCostAmount", RealEstateDisplayUtils.formatPriceLabel(scenario.getTotalExtraCostAmount()));
        view.put("formattedTotalRequiredCashAmount", RealEstateDisplayUtils.formatPriceLabel(scenario.getTotalRequiredCashAmount()));
        view.put("formattedAcquisitionTaxRate", RealEstateDisplayUtils.formatPercent(scenario.getAcquisitionTaxRate() * 100));
        view.put("formattedBrokerageFeeRate", RealEstateDisplayUtils.formatPercent(scenario.getBrokerageFeeRate() * 100));
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
        property.put("purchaseCostSupported", RealEstatePurchaseCostCalculator.isSupportedProperty(propertyCategory, transactionType));
        property.put("kakaoMapSearchUrl", buildKakaoMapSearchUrl(property));
    }

    private String buildKakaoMapSearchUrl(Map<String, Object> property) {
        String searchKeyword = trimToNull(defaultString(property.get("address")));
        if (searchKeyword == null) {
            searchKeyword = trimToNull(defaultString(property.get("displayName")));
        }
        if (searchKeyword == null) {
            return "";
        }
        return KAKAO_MAP_SEARCH_BASE_URL + UriUtils.encodePathSegment(searchKeyword, StandardCharsets.UTF_8);
    }

    private List<Map<String, String>> toMonthOptions(List<String> yearMonths) {
        return yearMonths.stream()
                .map(value -> Map.of(
                        "value", value,
                        "label", RealEstateDisplayUtils.formatYearMonthLabel(value)
                ))
                .toList();
    }

    private List<Map<String, String>> toStringOptions(List<String> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .filter(this::hasText)
                .map(value -> Map.of("value", value, "label", value))
                .toList();
    }

    private List<Map<String, Object>> defaultList(List<Map<String, Object>> values) {
        return values == null ? List.of() : values;
    }

    private List<Map<String, String>> createCategoryOptions() {
        return List.of(
                Map.of("value", "apartment", "label", RealEstateDisplayUtils.getCategoryLabel("apartment")),
                Map.of("value", "rowhouse", "label", RealEstateDisplayUtils.getCategoryLabel("rowhouse")),
                Map.of("value", "officetel", "label", RealEstateDisplayUtils.getCategoryLabel("officetel")),
                Map.of("value", "detached-house", "label", RealEstateDisplayUtils.getCategoryLabel("detached-house")),
                Map.of("value", "commercial", "label", RealEstateDisplayUtils.getCategoryLabel("commercial"))
        );
    }

    private List<Map<String, String>> createTransactionOptions() {
        return List.of(
                Map.of("value", "trade", "label", RealEstateDisplayUtils.getTransactionLabel("trade")),
                Map.of("value", "rent", "label", RealEstateDisplayUtils.getTransactionLabel("rent"))
        );
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

    private CmsUserRealEstateSearchParam normalizeSearchParam(CmsUserRealEstateSearchParam source, String latestMonth) {
        CmsUserRealEstateSearchParam safeSource = source == null ? new CmsUserRealEstateSearchParam() : source;
        Integer minAmount = normalizePositiveInt(safeSource.getMinAmount());
        Integer maxAmount = normalizePositiveInt(safeSource.getMaxAmount());
        if (minAmount != null && maxAmount != null && minAmount > maxAmount) {
            int oldMinAmount = minAmount;
            minAmount = maxAmount;
            maxAmount = oldMinAmount;
        }

        BigDecimal minAreaPyeong = normalizePositiveDecimal(safeSource.getMinAreaPyeong());
        BigDecimal maxAreaPyeong = normalizePositiveDecimal(safeSource.getMaxAreaPyeong());
        if (minAreaPyeong != null && maxAreaPyeong != null && minAreaPyeong.compareTo(maxAreaPyeong) > 0) {
            BigDecimal oldMinAreaPyeong = minAreaPyeong;
            minAreaPyeong = maxAreaPyeong;
            maxAreaPyeong = oldMinAreaPyeong;
        }

        Integer minBuildYear = normalizePositiveInt(safeSource.getMinBuildYear());
        Integer maxBuildYear = normalizePositiveInt(safeSource.getMaxBuildYear());
        if (minBuildYear != null && maxBuildYear != null && minBuildYear > maxBuildYear) {
            int oldMinBuildYear = minBuildYear;
            minBuildYear = maxBuildYear;
            maxBuildYear = oldMinBuildYear;
        }

        return CmsUserRealEstateSearchParam.builder()
                .keyword(trimToNull(safeSource.getKeyword()))
                .dealYearMonth(hasText(safeSource.getDealYearMonth()) ? safeSource.getDealYearMonth().trim() : latestMonth)
                .sort(normalizeSort(safeSource.getSort()))
                .page(safeSource.getPage())
                .sggName(trimToNull(safeSource.getSggName()))
                .umdName(trimToNull(safeSource.getUmdName()))
                .propertyCategory(normalizeAllowedValue(safeSource.getPropertyCategory(), PROPERTY_CATEGORIES))
                .transactionType(normalizeAllowedValue(safeSource.getTransactionType(), TRANSACTION_TYPES))
                .minAmount(minAmount)
                .maxAmount(maxAmount)
                .minAreaPyeong(minAreaPyeong)
                .maxAreaPyeong(maxAreaPyeong)
                .minBuildYear(minBuildYear)
                .maxBuildYear(maxBuildYear)
                .build();
    }

    private Map<String, Object> createFilterMap(CmsUserRealEstateSearchParam searchParam) {
        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("keyword", defaultString(searchParam.getKeyword()));
        filters.put("dealYearMonth", defaultString(searchParam.getDealYearMonth()));
        filters.put("sort", defaultString(searchParam.getSort()));
        filters.put("sggName", defaultString(searchParam.getSggName()));
        filters.put("umdName", defaultString(searchParam.getUmdName()));
        filters.put("propertyCategory", defaultString(searchParam.getPropertyCategory()));
        filters.put("transactionType", defaultString(searchParam.getTransactionType()));
        filters.put("minAmount", defaultString(searchParam.getMinAmount()));
        filters.put("maxAmount", defaultString(searchParam.getMaxAmount()));
        filters.put("minAreaPyeong", defaultString(searchParam.getMinAreaPyeong()));
        filters.put("maxAreaPyeong", defaultString(searchParam.getMaxAreaPyeong()));
        filters.put("minBuildYear", defaultString(searchParam.getMinBuildYear()));
        filters.put("maxBuildYear", defaultString(searchParam.getMaxBuildYear()));
        return filters;
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

    private String formatSignedPriceLabel(int value) {
        if (value > 0) {
            return "+" + RealEstateDisplayUtils.formatPriceLabel(value);
        }
        if (value < 0) {
            return "-" + RealEstateDisplayUtils.formatPriceLabel(Math.abs(value));
        }
        return "변동 없음";
    }

    private String formatSignedPercent(double value) {
        if (Math.abs(value) < 0.005) {
            return "0%";
        }
        String formatted = RealEstateDisplayUtils.formatPercent(Math.abs(value));
        return value > 0 ? "+" + formatted : "-" + formatted;
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

    private String trimToNull(String value) {
        if (!hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String normalizeAllowedValue(String value, Set<String> allowedValues) {
        String normalized = trimToNull(value);
        if (normalized == null || !allowedValues.contains(normalized)) {
            return null;
        }
        return normalized;
    }

    private Integer normalizePositiveInt(Integer value) {
        if (value == null || value < 0) {
            return null;
        }
        return value;
    }

    private BigDecimal normalizePositiveDecimal(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            return null;
        }
        return value;
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
