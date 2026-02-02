package co.grap.pack.qrgen.visitor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 방문자 추적 데이터 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrGenVisitor {

    /** 방문자 ID (PK) */
    private Long qrGenVisitorId;

    /** 세션 ID */
    private String qrGenVisitorSessionId;

    /** 로그인 사용자 ID (익명일 경우 null) */
    private Long qrGenVisitorUserId;

    /** IP 주소 */
    private String qrGenVisitorIpAddress;

    /** User-Agent 문자열 */
    private String qrGenVisitorUserAgent;

    /** 방문 페이지 URL */
    private String qrGenVisitorPageUrl;

    /** 리퍼러 URL */
    private String qrGenVisitorReferrer;

    /** 브라우저명 */
    private String qrGenVisitorBrowserName;

    /** 브라우저 버전 */
    private String qrGenVisitorBrowserVersion;

    /** OS명 */
    private String qrGenVisitorOsName;

    /** OS 버전 */
    private String qrGenVisitorOsVersion;

    /** 디바이스 타입 */
    private QrGenVisitorDeviceType qrGenVisitorDeviceType;

    /** 화면 해상도 */
    private String qrGenVisitorScreenResolution;

    /** 브라우저 언어 */
    private String qrGenVisitorLanguage;

    /** 방문 시간 */
    private LocalDateTime qrGenVisitorVisitedAt;

    /** 체류 시간 (초) */
    private Integer qrGenVisitorDurationSeconds;
}
