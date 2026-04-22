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

    /** BOT 요청 수 */
    private Long botPv;

    /** BOT 세션 수 */
    private Long botUv;
}
