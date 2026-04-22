package co.grap.pack.common.visitor.service;

import co.grap.pack.common.visitor.mapper.PackVisitorMapper;
import co.grap.pack.common.visitor.model.PackVisitor;
import co.grap.pack.common.visitor.model.PackVisitorAuthScope;
import co.grap.pack.common.visitor.model.PackVisitorClassification;
import co.grap.pack.common.visitor.model.PackVisitorDeviceType;
import co.grap.pack.common.visitor.model.PackVisitorUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 공통 방문자 추적 서비스다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PackVisitorService {

    private static final Pattern BROWSER_CHROME = Pattern.compile("Chrome/([\\d.]+)");
    private static final Pattern BROWSER_FIREFOX = Pattern.compile("Firefox/([\\d.]+)");
    private static final Pattern BROWSER_SAFARI = Pattern.compile("Version/([\\d.]+).*Safari");
    private static final Pattern BROWSER_EDGE = Pattern.compile("Edg/([\\d.]+)");
    private static final Pattern BROWSER_OPERA = Pattern.compile("OPR/([\\d.]+)");

    private static final Pattern OS_WINDOWS = Pattern.compile("Windows NT ([\\d.]+)");
    private static final Pattern OS_MAC = Pattern.compile("Mac OS X ([\\d_]+)");
    private static final Pattern OS_LINUX = Pattern.compile("Linux");
    private static final Pattern OS_ANDROID = Pattern.compile("Android ([\\d.]+)");
    private static final Pattern OS_IOS = Pattern.compile("iPhone OS ([\\d_]+)|iPad.*OS ([\\d_]+)");

    private final PackVisitorMapper packVisitorMapper;

    /**
     * 방문 기록을 저장한다.
     */
    @Transactional
    public Long recordPackVisitor(
            HttpServletRequest request,
            String sessionId,
            PackVisitorClassification classification,
            PackVisitorAuthScope authScope,
            Long authUserId
    ) {
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = getClientIpAddress(request);
        String pageUrl = buildPageUrl(request);
        String referrer = request.getHeader("Referer");

        String[] browserInfo = parseBrowser(userAgent);
        String[] osInfo = parseOs(userAgent);
        PackVisitorDeviceType deviceType = parseDeviceType(userAgent);

        PackVisitor visitor = PackVisitor.builder()
                .sessionId(sessionId)
                .authScope(authScope)
                .authUserId(authUserId)
                .serviceCode(classification.getServiceCode())
                .menuCode(classification.getMenuCode())
                .routeKey(classification.getRouteKey())
                .pageUrl(pageUrl)
                .referrer(referrer)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .browserName(browserInfo[0])
                .browserVersion(browserInfo[1])
                .osName(osInfo[0])
                .osVersion(osInfo[1])
                .deviceType(deviceType)
                .visitedAt(LocalDateTime.now())
                .build();

        packVisitorMapper.insertPackVisitor(visitor);

        log.info(
                "✅ [CHECK] 공통 방문자 기록 완료: visitorId={}, service={}, menu={}, routeKey={}",
                visitor.getId(),
                classification.getServiceCode(),
                classification.getMenuCode(),
                classification.getRouteKey()
        );

        return visitor.getId();
    }

    /**
     * 체류시간과 클라이언트 정보를 갱신한다.
     */
    @Transactional
    public void updatePackVisitorDuration(PackVisitorUpdateRequest updateRequest) {
        int durationSeconds = Math.max(0, updateRequest.getDurationSeconds() == null ? 0 : updateRequest.getDurationSeconds());

        packVisitorMapper.updatePackVisitorDuration(
                updateRequest.getVisitorId(),
                durationSeconds,
                normalizeNullable(updateRequest.getScreenResolution()),
                normalizeNullable(updateRequest.getLanguage())
        );

        log.info(
                "✅ [CHECK] 공통 방문자 체류시간 업데이트: visitorId={}, durationSeconds={}",
                updateRequest.getVisitorId(),
                durationSeconds
        );
    }

    private String buildPageUrl(HttpServletRequest request) {
        String pageUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isBlank()) {
            pageUrl += "?" + queryString;
        }
        return pageUrl;
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
                if (ip.contains(",")) {
                    return ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    private String[] parseBrowser(String userAgent) {
        if (userAgent == null) {
            return new String[]{"Unknown", null};
        }

        Matcher edgeMatcher = BROWSER_EDGE.matcher(userAgent);
        if (edgeMatcher.find()) {
            return new String[]{"Edge", edgeMatcher.group(1)};
        }

        Matcher operaMatcher = BROWSER_OPERA.matcher(userAgent);
        if (operaMatcher.find()) {
            return new String[]{"Opera", operaMatcher.group(1)};
        }

        Matcher chromeMatcher = BROWSER_CHROME.matcher(userAgent);
        if (chromeMatcher.find()) {
            return new String[]{"Chrome", chromeMatcher.group(1)};
        }

        Matcher firefoxMatcher = BROWSER_FIREFOX.matcher(userAgent);
        if (firefoxMatcher.find()) {
            return new String[]{"Firefox", firefoxMatcher.group(1)};
        }

        Matcher safariMatcher = BROWSER_SAFARI.matcher(userAgent);
        if (safariMatcher.find()) {
            return new String[]{"Safari", safariMatcher.group(1)};
        }

        return new String[]{"Unknown", null};
    }

    private String[] parseOs(String userAgent) {
        if (userAgent == null) {
            return new String[]{"Unknown", null};
        }

        Matcher androidMatcher = OS_ANDROID.matcher(userAgent);
        if (androidMatcher.find()) {
            return new String[]{"Android", androidMatcher.group(1)};
        }

        Matcher iosMatcher = OS_IOS.matcher(userAgent);
        if (iosMatcher.find()) {
            String version = iosMatcher.group(1) != null ? iosMatcher.group(1) : iosMatcher.group(2);
            if (version != null) {
                version = version.replace("_", ".");
            }
            return new String[]{"iOS", version};
        }

        Matcher windowsMatcher = OS_WINDOWS.matcher(userAgent);
        if (windowsMatcher.find()) {
            return new String[]{"Windows", mapWindowsVersion(windowsMatcher.group(1))};
        }

        Matcher macMatcher = OS_MAC.matcher(userAgent);
        if (macMatcher.find()) {
            return new String[]{"macOS", macMatcher.group(1).replace("_", ".")};
        }

        if (OS_LINUX.matcher(userAgent).find()) {
            return new String[]{"Linux", null};
        }

        return new String[]{"Unknown", null};
    }

    private String mapWindowsVersion(String ntVersion) {
        return switch (ntVersion) {
            case "10.0" -> "10/11";
            case "6.3" -> "8.1";
            case "6.2" -> "8";
            case "6.1" -> "7";
            case "6.0" -> "Vista";
            case "5.1", "5.2" -> "XP";
            default -> ntVersion;
        };
    }

    private PackVisitorDeviceType parseDeviceType(String userAgent) {
        if (userAgent == null) {
            return PackVisitorDeviceType.UNKNOWN;
        }

        String lowerCaseUserAgent = userAgent.toLowerCase();

        if (lowerCaseUserAgent.contains("bot")
                || lowerCaseUserAgent.contains("crawler")
                || lowerCaseUserAgent.contains("spider")
                || lowerCaseUserAgent.contains("googlebot")
                || lowerCaseUserAgent.contains("bingbot")
                || lowerCaseUserAgent.contains("yandex")) {
            return PackVisitorDeviceType.BOT;
        }

        if (lowerCaseUserAgent.contains("ipad")
                || lowerCaseUserAgent.contains("tablet")
                || (lowerCaseUserAgent.contains("android") && !lowerCaseUserAgent.contains("mobile"))) {
            return PackVisitorDeviceType.TABLET;
        }

        if (lowerCaseUserAgent.contains("mobile")
                || lowerCaseUserAgent.contains("iphone")
                || lowerCaseUserAgent.contains("ipod")
                || lowerCaseUserAgent.contains("android")
                || lowerCaseUserAgent.contains("blackberry")
                || lowerCaseUserAgent.contains("windows phone")) {
            return PackVisitorDeviceType.MOBILE;
        }

        if (lowerCaseUserAgent.contains("windows")
                || lowerCaseUserAgent.contains("macintosh")
                || lowerCaseUserAgent.contains("linux")) {
            return PackVisitorDeviceType.DESKTOP;
        }

        return PackVisitorDeviceType.UNKNOWN;
    }
}
