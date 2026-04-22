package co.grap.pack.qrmanage.superadmin.visitorstats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 라우트 상세 통계 모델이다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageSuperVisitorRouteStats {

    /** 라우트 키 */
    private String routeKey;

    /** PV */
    private Long pv;

    /** UV */
    private Long uv;

    /** 평균 체류시간(초) */
    private Double averageDurationSeconds;
}
