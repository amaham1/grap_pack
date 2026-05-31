package co.grap.pack.qrmanage.superadmin.visitorstats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IP 기준 최근 접속 기록 모델이다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageSuperVisitorIpAccessLog {

    /** 접속 시각 */
    private String visitedAt;

    /** IP 주소 */
    private String ipAddress;

    /** 방문 구분 */
    private String visitorTypeDisplayName;

    /** 서비스 코드 */
    private String serviceCode;

    /** 서비스 표시명 */
    private String serviceDisplayName;

    /** 메뉴 코드 */
    private String menuCode;

    /** 메뉴 표시명 */
    private String menuDisplayName;

    /** 상세 경로 */
    private String routeKey;

    /** 기기 코드 */
    private String deviceType;

    /** 기기 표시명 */
    private String deviceDisplayName;
}
