package co.grap.pack.common.visitor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공통 방문자 추적 모델이다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackVisitor {

    /** 방문 ID */
    private Long id;

    /** 세션 ID */
    private String sessionId;

    /** 인증 범위 */
    private PackVisitorAuthScope authScope;

    /** 인증 사용자 ID */
    private Long authUserId;

    /** 서비스 코드 */
    private PackVisitorServiceCode serviceCode;

    /** 메뉴 코드 */
    private PackVisitorMenuCode menuCode;

    /** 정규화한 라우트 키 */
    private String routeKey;

    /** 실제 방문 URL */
    private String pageUrl;

    /** 리퍼러 */
    private String referrer;

    /** IP 주소 */
    private String ipAddress;

    /** User-Agent */
    private String userAgent;

    /** 브라우저명 */
    private String browserName;

    /** 브라우저 버전 */
    private String browserVersion;

    /** OS명 */
    private String osName;

    /** OS 버전 */
    private String osVersion;

    /** 디바이스 유형 */
    private PackVisitorDeviceType deviceType;

    /** 화면 해상도 */
    private String screenResolution;

    /** 브라우저 언어 */
    private String language;

    /** 방문 시각 */
    private LocalDateTime visitedAt;

    /** 체류시간(초) */
    private Integer durationSeconds;

    /** 레거시 소스 */
    private String legacySource;

    /** 레거시 소스 ID */
    private Long legacySourceId;
}
