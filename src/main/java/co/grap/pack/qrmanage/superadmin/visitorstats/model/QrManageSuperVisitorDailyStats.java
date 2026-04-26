package co.grap.pack.qrmanage.superadmin.visitorstats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 일별 방문자 추이 모델이다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageSuperVisitorDailyStats {

    /** 집계 일자 */
    private String date;

    /** 방문 수(BOT 제외) */
    private Long pv;

    /** 중복 제외 방문자 수(BOT 제외) */
    private Long uv;

    /** 사람 추정 방문 수 */
    private Long humanVerifiedPv;

    /** 사람 추정 세션 수 */
    private Long humanVerifiedUv;

    /** BOT 요청 수 */
    private Long botPv;

    /** BOT 세션 수 */
    private Long botUv;
}
