package co.grap.pack.grap.user.content.service;

import co.grap.pack.grap.user.content.mapper.CmsUserRealEstateMapper;
import co.grap.pack.grap.user.content.model.CmsUserRealEstateSearchParam;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CmsUserRealEstateServiceTest {

    @Mock
    private CmsUserRealEstateMapper realEstateMapper;

    private CmsUserRealEstateService service;

    @BeforeEach
    void setUp() {
        service = new CmsUserRealEstateService(realEstateMapper, new ObjectMapper());
    }

    @Test
    void listUsesLatestMonthWhenFilterIsMissing() {
        when(realEstateMapper.selectLatestDealYearMonth()).thenReturn("202604");
        when(realEstateMapper.selectAvailableDealYearMonths()).thenReturn(List.of("202604", "202603"));
        when(realEstateMapper.selectAvailableSggNames("202604")).thenReturn(List.of("제주시"));
        when(realEstateMapper.selectAvailableUmdNames("202604", null)).thenReturn(List.of("아라일동"));
        when(realEstateMapper.selectRealEstateCount(any(CmsUserRealEstateSearchParam.class))).thenReturn(1);
        when(realEstateMapper.selectRealEstateList(any(CmsUserRealEstateSearchParam.class), eq(0), eq(12))).thenReturn(List.of(new LinkedHashMap<>(Map.ofEntries(
                Map.entry("id", 1L),
                Map.entry("datasetId", "apt-trade"),
                Map.entry("propertyCategory", "apartment"),
                Map.entry("transactionType", "trade"),
                Map.entry("sggName", "제주시"),
                Map.entry("umdName", "아라일동"),
                Map.entry("address", "제주특별자치도 제주시 아라일동 101-3"),
                Map.entry("displayName", "테스트 아파트"),
                Map.entry("areaM2", new BigDecimal("84.92")),
                Map.entry("dealDate", Date.valueOf(LocalDate.of(2026, 4, 20))),
                Map.entry("floor", 2),
                Map.entry("buildYear", 2018),
                Map.entry("tradeAmountManwon", 11000),
                Map.entry("depositAmountManwon", 0),
                Map.entry("monthlyRentManwon", 0)
        ))));

        Map<String, Object> result = service.getRealEstateList("", null, "latest", 1);

        assertThat(result.get("currentPropertyMonth")).isEqualTo("202604");
        assertThat(result.get("formattedCurrentPropertyMonth")).isEqualTo("2026년 4월");
        assertThat(((List<?>) result.get("propertyList"))).hasSize(1);
        Map<?, ?> property = (Map<?, ?>) ((List<?>) result.get("propertyList")).get(0);
        assertThat(property.get("formattedDisplayAmount")).isEqualTo("1억 1천만원");
        assertThat(property.get("formattedAreaLabel")).isEqualTo("84.92㎡ (25.69평)");
    }

    @Test
    void listNormalizesAdvancedFiltersAndBuildsOptions() {
        when(realEstateMapper.selectLatestDealYearMonth()).thenReturn("202604");
        when(realEstateMapper.selectAvailableDealYearMonths()).thenReturn(List.of("202604"));
        when(realEstateMapper.selectAvailableSggNames("202604")).thenReturn(List.of("서귀포시", "제주시"));
        when(realEstateMapper.selectAvailableUmdNames("202604", "제주시")).thenReturn(List.of("노형동"));
        when(realEstateMapper.selectRealEstateCount(argThat(param ->
                "제주시".equals(param.getSggName())
                        && "노형동".equals(param.getUmdName())
                        && "apartment".equals(param.getPropertyCategory())
                        && "trade".equals(param.getTransactionType())
                        && Integer.valueOf(10000).equals(param.getMinAmount())
                        && Integer.valueOf(30000).equals(param.getMaxAmount())
                        && Integer.valueOf(1990).equals(param.getMinBuildYear())
                        && Integer.valueOf(2020).equals(param.getMaxBuildYear())
        ))).thenReturn(0);

        CmsUserRealEstateSearchParam searchParam = CmsUserRealEstateSearchParam.builder()
                .dealYearMonth("202604")
                .sggName(" 제주시 ")
                .umdName("노형동")
                .propertyCategory("apartment")
                .transactionType("trade")
                .minAmount(30000)
                .maxAmount(10000)
                .minBuildYear(2020)
                .maxBuildYear(1990)
                .build();

        Map<String, Object> result = service.getRealEstateList(searchParam);

        @SuppressWarnings("unchecked")
        Map<String, Object> filters = (Map<String, Object>) result.get("filters");
        assertThat(filters.get("sggName")).isEqualTo("제주시");
        assertThat(filters.get("minAmount")).isEqualTo("10000");
        assertThat(filters.get("maxAmount")).isEqualTo("30000");
        assertThat(filters.get("minBuildYear")).isEqualTo("1990");
        assertThat(filters.get("maxBuildYear")).isEqualTo("2020");
        assertThat((List<?>) result.get("sggOptions")).hasSize(2);
        assertThat((List<?>) result.get("categoryOptions")).isNotEmpty();
    }

    @Test
    void detailBuildsPriceSeriesAndMortgageViewModel() {
        when(realEstateMapper.selectRealEstateById(1L)).thenReturn(new LinkedHashMap<>(Map.ofEntries(
                Map.entry("id", 1L),
                Map.entry("propertyMatchKey", "apt-trade|50110|test"),
                Map.entry("datasetId", "apt-trade"),
                Map.entry("propertyCategory", "apartment"),
                Map.entry("transactionType", "trade"),
                Map.entry("sggName", "제주시"),
                Map.entry("umdName", "아라일동"),
                Map.entry("address", "제주특별자치도 제주시 아라일동 101-3"),
                Map.entry("displayName", "테스트 아파트"),
                Map.entry("areaM2", new BigDecimal("84.92")),
                Map.entry("dealDate", Date.valueOf(LocalDate.of(2026, 4, 20))),
                Map.entry("floor", 2),
                Map.entry("buildYear", 2018),
                Map.entry("tradeAmountManwon", 54500),
                Map.entry("depositAmountManwon", 0),
                Map.entry("monthlyRentManwon", 0),
                Map.entry("dealYearMonth", "202604")
        )));
        when(realEstateMapper.selectPropertyAvailableYears("apt-trade|50110|test")).thenReturn(List.of(2026, 2025));
        when(realEstateMapper.selectPropertyMonthlyAverageHistory("apt-trade|50110|test", 2026, "trade")).thenReturn(List.of(
                Map.of("dealMonth", 3, "averageAmount", 54000, "minAmount", 53500, "maxAmount", 54500, "transactionCount", 2),
                Map.of("dealMonth", 4, "averageAmount", 54500, "minAmount", 54500, "maxAmount", 54500, "transactionCount", 1)
        ));
        when(realEstateMapper.selectSamePropertyRecentTransactions("apt-trade|50110|test", 1L, 8)).thenReturn(List.of(
                new LinkedHashMap<>(Map.ofEntries(
                        Map.entry("id", 2L),
                        Map.entry("datasetId", "apt-trade"),
                        Map.entry("propertyCategory", "apartment"),
                        Map.entry("transactionType", "trade"),
                        Map.entry("sggName", "제주시"),
                        Map.entry("umdName", "아라일동"),
                        Map.entry("address", "제주특별자치도 제주시 아라일동 101-3"),
                        Map.entry("displayName", "테스트 아파트"),
                        Map.entry("areaM2", new BigDecimal("84.92")),
                        Map.entry("dealDate", Date.valueOf(LocalDate.of(2026, 3, 20))),
                        Map.entry("dealYearMonth", "202603"),
                        Map.entry("floor", 2),
                        Map.entry("buildYear", 2018),
                        Map.entry("tradeAmountManwon", 54000),
                        Map.entry("depositAmountManwon", 0),
                        Map.entry("monthlyRentManwon", 0)
                ))
        ));
        when(realEstateMapper.selectSimilarConditionTransactions(any(), eq(8))).thenReturn(List.of(
                new LinkedHashMap<>(Map.ofEntries(
                        Map.entry("id", 3L),
                        Map.entry("datasetId", "apt-trade"),
                        Map.entry("propertyCategory", "apartment"),
                        Map.entry("transactionType", "trade"),
                        Map.entry("sggName", "제주시"),
                        Map.entry("umdName", "아라일동"),
                        Map.entry("address", "제주특별자치도 제주시 아라일동 202-1"),
                        Map.entry("displayName", "비교 아파트"),
                        Map.entry("areaM2", new BigDecimal("82.00")),
                        Map.entry("dealDate", Date.valueOf(LocalDate.of(2026, 4, 5))),
                        Map.entry("dealYearMonth", "202604"),
                        Map.entry("floor", 5),
                        Map.entry("buildYear", 2017),
                        Map.entry("tradeAmountManwon", 53000),
                        Map.entry("depositAmountManwon", 0),
                        Map.entry("monthlyRentManwon", 0)
                ))
        ));

        Map<String, Object> result = service.getRealEstateDetail(1L, null);

        assertThat(result.get("selectedYear")).isEqualTo(2026);
        @SuppressWarnings("unchecked")
        List<Integer> availableYears = (List<Integer>) result.get("availableYears");
        assertThat(availableYears).containsExactly(2026, 2025);
        assertThat((List<?>) result.get("priceSeries")).hasSize(2);
        Map<?, ?> trendSummary = (Map<?, ?>) result.get("trendSummary");
        assertThat(trendSummary.get("formattedTransactionCount")).isEqualTo("3");
        Map<?, ?> property = (Map<?, ?>) result.get("property");
        assertThat(property.get("mortgageSupported")).isEqualTo(true);
        assertThat(property.get("purchaseCostSupported")).isEqualTo(true);
        assertThat((List<?>) result.get("samePropertyTransactions")).hasSize(1);
        assertThat((List<?>) result.get("similarConditionTransactions")).hasSize(1);
        Map<?, ?> mortgageScenario = (Map<?, ?>) result.get("mortgageScenario");
        assertThat(mortgageScenario.get("buyerProfileLabel")).isEqualTo("무주택");
        assertThat(mortgageScenario.get("areaPolicyLabel")).isEqualTo("제주/지방 비규제지역");
        assertThat(result.get("purchaseCostScenario")).isNotNull();
    }
}
