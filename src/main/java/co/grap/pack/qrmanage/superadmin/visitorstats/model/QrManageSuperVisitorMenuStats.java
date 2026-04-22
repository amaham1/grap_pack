package co.grap.pack.qrmanage.superadmin.visitorstats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 서비스/메뉴별 방문자 통계 모델이다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageSuperVisitorMenuStats {

    /** 서비스 코드 */
    private String serviceCode;

    /** 서비스명 */
    private String serviceDisplayName;

    /** 메뉴 코드 */
    private String menuCode;

    /** 메뉴명 */
    private String menuDisplayName;

    /** PV */
    private Long pv;

    /** UV */
    private Long uv;

    /** 평균 체류시간(초) */
    private Double averageDurationSeconds;

    /** 모바일 비율 */
    private Double mobileRatio;
}
