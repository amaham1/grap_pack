package co.grap.pack.admin.dashboard.service;

import co.grap.pack.admin.dashboard.mapper.AdminDashboardMapper;
import co.grap.pack.admin.dashboard.model.AdminDashboardDailyCount;
import co.grap.pack.admin.dashboard.model.AdminDashboardSummary;
import co.grap.pack.admin.qrgen.mapper.AdminQrGenQueryMapper;
import co.grap.pack.common.visitor.model.PackVisitorServiceCode;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShopStatus;
import co.grap.pack.qrmanage.shopadmin.stats.service.QrManageStatsService;
import co.grap.pack.qrmanage.superadmin.shop.model.QrManageShopSearchParam;
import co.grap.pack.qrmanage.superadmin.shop.service.QrManageSuperShopService;
import co.grap.pack.qrmanage.superadmin.visitorstats.service.QrManageSuperVisitorStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 통합 대시보드 서비스다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {

    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final AdminDashboardMapper adminDashboardMapper;
    private final QrManageSuperShopService qrManageSuperShopService;
    private final QrManageStatsService qrManageStatsService;
    private final AdminQrGenQueryMapper adminQrGenQueryMapper;
    private final QrManageSuperVisitorStatsService visitorStatsService;

    /**
     * 통합 대시보드 요약을 생성한다.
     */
    public AdminDashboardSummary getSummary() {
        LocalDate endDate = LocalDate.now(KOREA_ZONE_ID);
        LocalDate startDate = endDate.minusDays(6);

        Map<String, Integer> qrStats = qrManageStatsService.getSystemDashboardStats();
        List<AdminDashboardDailyCount> qrGenDailyCounts = normalizeDailyCounts(
                startDate,
                endDate,
                adminQrGenQueryMapper.selectDailyHistoryCounts(startDate.toString(), endDate.toString())
        );

        return AdminDashboardSummary.builder()
                .publishedContentCount(orZero(adminDashboardMapper.countPublishedContents()))
                .unpublishedContentCount(orZero(adminDashboardMapper.countUnpublishedContents()))
                .pendingExternalCount(orZero(adminDashboardMapper.countPendingExternalData()))
                .pendingShopCount((long) qrManageSuperShopService.countPending())
                .hiddenShopCount((long) qrManageSuperShopService.countAll(QrManageShopSearchParam.builder()
                        .isVisible(false)
                        .build()))
                .todayQrScanCount((long) qrStats.getOrDefault("today", 0))
                .qrGenUserCount(orZero(adminQrGenQueryMapper.countUsers(null, null)))
                .todayQrGenHistoryCount(orZero(adminQrGenQueryMapper.countTodayHistories()))
                .visitorTodayPv(visitorStatsService.getDashboardStats(
                        endDate,
                        endDate,
                        null,
                        null
                ).getTodayPv())
                .visitorTodayUv(visitorStatsService.getDashboardStats(
                        endDate,
                        endDate,
                        null,
                        null
                ).getTodayUv())
                .qrGenDailyCounts(qrGenDailyCounts)
                .visitorDailyStats(visitorStatsService.getDailyStats(
                        startDate,
                        endDate,
                        PackVisitorServiceCode.QRGEN,
                        null
                ))
                .build();
    }

    private long orZero(Long value) {
        return value != null ? value : 0L;
    }

    private List<AdminDashboardDailyCount> normalizeDailyCounts(
            LocalDate startDate,
            LocalDate endDate,
            List<AdminDashboardDailyCount> rawCounts
    ) {
        Map<String, AdminDashboardDailyCount> countByDate = rawCounts.stream()
                .collect(Collectors.toMap(AdminDashboardDailyCount::getDate, Function.identity()));

        List<AdminDashboardDailyCount> normalizedCounts = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            String date = currentDate.toString();
            AdminDashboardDailyCount dailyCount = countByDate.getOrDefault(date,
                    AdminDashboardDailyCount.builder().date(date).count(0L).build());
            normalizedCounts.add(dailyCount);
            currentDate = currentDate.plusDays(1);
        }
        return normalizedCounts;
    }
}
