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

    /** PV */
    private Long pv;

    /** UV */
    private Long uv;
}
