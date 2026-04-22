package co.grap.pack.qrmanage.superadmin.visitorstats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 디바이스 유형별 방문 통계 모델이다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageSuperVisitorDeviceStats {

    /** 디바이스 코드 */
    private String deviceType;

    /** 디바이스명 */
    private String deviceDisplayName;

    /** PV */
    private Long pv;

    /** 비율 */
    private Double ratio;
}
