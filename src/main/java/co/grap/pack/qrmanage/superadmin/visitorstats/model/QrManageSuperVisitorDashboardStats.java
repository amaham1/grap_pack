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

    /** 기간 기준 전체 방문 수(BOT 제외) */
    private Long totalPv;

    /** 기간 기준 전체 중복 제외 방문자 수(BOT 제외) */
    private Long totalUv;

    /** 오늘 방문 수(BOT 제외) */
    private Long todayPv;

    /** 오늘 중복 제외 방문자 수(BOT 제외) */
    private Long todayUv;

    /** 기간 기준 전체 BOT 요청 수 */
    private Long totalBotPv;

    /** 기간 기준 전체 BOT 세션 수 */
    private Long totalBotUv;

    /** 오늘 BOT 요청 수 */
    private Long todayBotPv;

    /** 오늘 BOT 세션 수 */
    private Long todayBotUv;

    /** 평균 체류시간(초, BOT 제외) */
    private Double averageDurationSeconds;
}
