package co.grap.pack.admin.dashboard.model;

import co.grap.pack.qrmanage.superadmin.visitorstats.model.QrManageSuperVisitorDailyStats;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 통합 대시보드 요약 모델이다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardSummary {

    private Long publishedContentCount;
    private Long unpublishedContentCount;
    private Long pendingExternalCount;
    private Long pendingShopCount;
    private Long hiddenShopCount;
    private Long todayQrScanCount;
    private Long qrGenUserCount;
    private Long todayQrGenHistoryCount;
    private Long visitorTodayPv;
    private Long visitorTodayUv;
    private Long visitorTodayBotPv;
    private Long visitorTodayBotUv;
    private List<AdminDashboardDailyCount> qrGenDailyCounts;
    private List<QrManageSuperVisitorDailyStats> visitorDailyStats;
}
