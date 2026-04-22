package co.grap.pack.qrmanage.superadmin.visitorstats.service;

import co.grap.pack.qrmanage.superadmin.visitorstats.mapper.QrManageSuperVisitorStatsMapper;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorDailyStats;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorDashboardStats;
import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorDeviceStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QrManageSuperVisitorStatsServiceTest {

    @Mock
    private QrManageSuperVisitorStatsMapper visitorStatsMapper;

    private QrManageSuperVisitorStatsService visitorStatsService;

    @BeforeEach
    void setUp() {
        visitorStatsService = new QrManageSuperVisitorStatsService(visitorStatsMapper);
    }

    @Test
    void separatesBotCountsInDashboardStats() {
        when(visitorStatsMapper.selectDashboardStats(anyString(), anyString(), isNull(), isNull()))
                .thenReturn(QrManageSuperVisitorDashboardStats.builder()
                        .totalPv(12L)
                        .totalUv(8L)
                        .totalBotPv(5L)
                        .totalBotUv(3L)
                        .averageDurationSeconds(48.2)
                        .build());
        when(visitorStatsMapper.selectTodayStats(anyString(), isNull(), isNull()))
                .thenReturn(QrManageSuperVisitorDashboardStats.builder()
                        .todayPv(3L)
                        .todayUv(2L)
                        .todayBotPv(4L)
                        .todayBotUv(2L)
                        .build());

        QrManageSuperVisitorDashboardStats result = visitorStatsService.getDashboardStats(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 22),
                null,
                null
        );

        assertThat(result.getTotalPv()).isEqualTo(12L);
        assertThat(result.getTotalUv()).isEqualTo(8L);
        assertThat(result.getTotalBotPv()).isEqualTo(5L);
        assertThat(result.getTotalBotUv()).isEqualTo(3L);
        assertThat(result.getTodayPv()).isEqualTo(3L);
        assertThat(result.getTodayUv()).isEqualTo(2L);
        assertThat(result.getTodayBotPv()).isEqualTo(4L);
        assertThat(result.getTodayBotUv()).isEqualTo(2L);
        assertThat(result.getAverageDurationSeconds()).isEqualTo(48.2);
    }

    @Test
    void fillsMissingDailyStatsIncludingBotCounts() {
        when(visitorStatsMapper.selectDailyStats(anyString(), anyString(), isNull(), isNull()))
                .thenReturn(List.of(
                        QrManageSuperVisitorDailyStats.builder()
                                .date("2026-04-20")
                                .pv(7L)
                                .uv(5L)
                                .botPv(2L)
                                .botUv(1L)
                                .build()
                ));

        List<QrManageSuperVisitorDailyStats> result = visitorStatsService.getDailyStats(
                LocalDate.of(2026, 4, 20),
                LocalDate.of(2026, 4, 21),
                null,
                null
        );

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDate()).isEqualTo("2026-04-20");
        assertThat(result.get(0).getBotPv()).isEqualTo(2L);
        assertThat(result.get(0).getBotUv()).isEqualTo(1L);
        assertThat(result.get(1).getDate()).isEqualTo("2026-04-21");
        assertThat(result.get(1).getPv()).isZero();
        assertThat(result.get(1).getUv()).isZero();
        assertThat(result.get(1).getBotPv()).isZero();
        assertThat(result.get(1).getBotUv()).isZero();
    }

    @Test
    void excludesBotFromDeviceStatsResultAndRatio() {
        when(visitorStatsMapper.selectDeviceStats(anyString(), anyString(), isNull(), isNull()))
                .thenReturn(List.of(
                        QrManageSuperVisitorDeviceStats.builder().deviceType("DESKTOP").pv(6L).build(),
                        QrManageSuperVisitorDeviceStats.builder().deviceType("MOBILE").pv(4L).build(),
                        QrManageSuperVisitorDeviceStats.builder().deviceType("BOT").pv(9L).build()
                ));

        List<QrManageSuperVisitorDeviceStats> result = visitorStatsService.getDeviceStats(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 22),
                null,
                null
        );

        assertThat(result).extracting(QrManageSuperVisitorDeviceStats::getDeviceType)
                .doesNotContain("BOT");
        assertThat(result).filteredOn(item -> "DESKTOP".equals(item.getDeviceType()))
                .singleElement()
                .extracting(QrManageSuperVisitorDeviceStats::getRatio)
                .isEqualTo(60.0);
        assertThat(result).filteredOn(item -> "MOBILE".equals(item.getDeviceType()))
                .singleElement()
                .extracting(QrManageSuperVisitorDeviceStats::getRatio)
                .isEqualTo(40.0);
    }
}
