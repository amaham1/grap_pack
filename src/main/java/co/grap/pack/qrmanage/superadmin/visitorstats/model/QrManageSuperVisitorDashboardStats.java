package co.grap.pack.qrmanage.superadmin.visitorstats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 슈퍼 관리자 방문자 요약 통계 모델이다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageSuperVisitorDashboardStats {

    /** 기간 기준 총 PV */
    private Long totalPv;

    /** 기간 기준 총 UV */
    private Long totalUv;

    /** 오늘 PV */
    private Long todayPv;

    /** 오늘 UV */
    private Long todayUv;

    /** 평균 체류시간(초) */
    private Double averageDurationSeconds;
}
