package co.grap.pack.grap.user.content.service;

import co.grap.pack.grap.user.content.mapper.CmsUserRealEstateMapper;
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
        when(realEstateMapper.selectRealEstateCount("", "202604")).thenReturn(1);
        when(realEstateMapper.selectRealEstateList("", "202604", "latest", 0, 12)).thenReturn(List.of(new LinkedHashMap<>(Map.ofEntries(
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
                Map.of("dealMonth", 3, "averageAmount", 54000),
                Map.of("dealMonth", 4, "averageAmount", 54500)
        ));

        Map<String, Object> result = service.getRealEstateDetail(1L, null);

        assertThat(result.get("selectedYear")).isEqualTo(2026);
        @SuppressWarnings("unchecked")
        List<Integer> availableYears = (List<Integer>) result.get("availableYears");
        assertThat(availableYears).containsExactly(2026, 2025);
        assertThat((List<?>) result.get("priceSeries")).hasSize(2);
        Map<?, ?> property = (Map<?, ?>) result.get("property");
        assertThat(property.get("mortgageSupported")).isEqualTo(true);
        Map<?, ?> mortgageScenario = (Map<?, ?>) result.get("mortgageScenario");
        assertThat(mortgageScenario.get("buyerProfileLabel")).isEqualTo("무주택");
        assertThat(mortgageScenario.get("areaPolicyLabel")).isEqualTo("제주/지방 비규제지역");
    }
}
