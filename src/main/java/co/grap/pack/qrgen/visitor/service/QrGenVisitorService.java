package co.grap.pack.qrgen.visitor.service;

import co.grap.pack.qrgen.visitor.mapper.QrGenVisitorMapper;
import co.grap.pack.qrgen.visitor.model.QrGenVisitor;
import co.grap.pack.qrgen.visitor.model.QrGenVisitorDeviceType;
import co.grap.pack.qrgen.visitor.model.QrGenVisitorUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 방문자 추적 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class QrGenVisitorService {

    private final QrGenVisitorMapper qrGenVisitorMapper;

    // User-Agent 파싱용 정규식
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

    /**
     * 방문자 기록 추가
     * @param request HTTP 요청
     * @param sessionId 세션 ID
     * @param userId 로그인 사용자 ID (null 가능)
     * @return 생성된 방문자 ID
     */
    @Transactional
    public Long recordQrGenVisitor(HttpServletRequest request, String sessionId, Long userId) {
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = getClientIpAddress(request);
        String pageUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null) {
            pageUrl += "?" + queryString;
        }
        String referrer = request.getHeader("Referer");

        // User-Agent 파싱
        String[] browserInfo = parseBrowser(userAgent);
        String[] osInfo = parseOs(userAgent);
        QrGenVisitorDeviceType deviceType = parseDeviceType(userAgent);

        QrGenVisitor visitor = QrGenVisitor.builder()
                .qrGenVisitorSessionId(sessionId)
                .qrGenVisitorUserId(userId)
                .qrGenVisitorIpAddress(ipAddress)
                .qrGenVisitorUserAgent(userAgent)
                .qrGenVisitorPageUrl(pageUrl)
                .qrGenVisitorReferrer(referrer)
                .qrGenVisitorBrowserName(browserInfo[0])
                .qrGenVisitorBrowserVersion(browserInfo[1])
                .qrGenVisitorOsName(osInfo[0])
                .qrGenVisitorOsVersion(osInfo[1])
                .qrGenVisitorDeviceType(deviceType)
                .qrGenVisitorVisitedAt(LocalDateTime.now())
                .build();

        qrGenVisitorMapper.insertQrGenVisitor(visitor);

        log.info("✅ [CHECK] 방문자 기록 완료: visitorId={}, ip={}, page={}, device={}",
                visitor.getQrGenVisitorId(), ipAddress, pageUrl, deviceType);

        return visitor.getQrGenVisitorId();
    }

    /**
     * 체류시간 및 클라이언트 정보 업데이트
     * @param updateRequest 업데이트 요청
     */
    @Transactional
    public void updateQrGenVisitorDuration(QrGenVisitorUpdateRequest updateRequest) {
        qrGenVisitorMapper.updateQrGenVisitorDuration(
                updateRequest.getVisitorId(),
                updateRequest.getDurationSeconds(),
                updateRequest.getScreenResolution(),
                updateRequest.getLanguage()
        );

        log.info("✅ [CHECK] 방문자 체류시간 업데이트: visitorId={}, duration={}초",
                updateRequest.getVisitorId(), updateRequest.getDurationSeconds());
    }

    /**
     * 방문자 조회
     * @param visitorId 방문자 ID
     * @return 방문자 정보
     */
    public QrGenVisitor findQrGenVisitorById(Long visitorId) {
        return qrGenVisitorMapper.findQrGenVisitorById(visitorId);
    }

    /**
     * 클라이언트 IP 주소 획득 (프록시 고려)
     */
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
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For 헤더는 여러 IP가 쉼표로 구분될 수 있음
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * 브라우저 정보 파싱
     * @return [브라우저명, 버전]
     */
    private String[] parseBrowser(String userAgent) {
        if (userAgent == null) {
            return new String[]{"Unknown", null};
        }

        // Edge 먼저 체크 (Chrome 포함하므로)
        Matcher edgeMatcher = BROWSER_EDGE.matcher(userAgent);
        if (edgeMatcher.find()) {
            return new String[]{"Edge", edgeMatcher.group(1)};
        }

        // Opera 체크 (Chrome 포함하므로)
        Matcher operaMatcher = BROWSER_OPERA.matcher(userAgent);
        if (operaMatcher.find()) {
            return new String[]{"Opera", operaMatcher.group(1)};
        }

        // Chrome 체크
        Matcher chromeMatcher = BROWSER_CHROME.matcher(userAgent);
        if (chromeMatcher.find()) {
            return new String[]{"Chrome", chromeMatcher.group(1)};
        }

        // Firefox 체크
        Matcher firefoxMatcher = BROWSER_FIREFOX.matcher(userAgent);
        if (firefoxMatcher.find()) {
            return new String[]{"Firefox", firefoxMatcher.group(1)};
        }

        // Safari 체크 (Chrome, Edge 등이 아닌 경우)
        Matcher safariMatcher = BROWSER_SAFARI.matcher(userAgent);
        if (safariMatcher.find()) {
            return new String[]{"Safari", safariMatcher.group(1)};
        }

        return new String[]{"Unknown", null};
    }

    /**
     * OS 정보 파싱
     * @return [OS명, 버전]
     */
    private String[] parseOs(String userAgent) {
        if (userAgent == null) {
            return new String[]{"Unknown", null};
        }

        // Android 체크
        Matcher androidMatcher = OS_ANDROID.matcher(userAgent);
        if (androidMatcher.find()) {
            return new String[]{"Android", androidMatcher.group(1)};
        }

        // iOS 체크
        Matcher iosMatcher = OS_IOS.matcher(userAgent);
        if (iosMatcher.find()) {
            String version = iosMatcher.group(1) != null ? iosMatcher.group(1) : iosMatcher.group(2);
            if (version != null) {
                version = version.replace("_", ".");
            }
            return new String[]{"iOS", version};
        }

        // Windows 체크
        Matcher windowsMatcher = OS_WINDOWS.matcher(userAgent);
        if (windowsMatcher.find()) {
            String version = mapWindowsVersion(windowsMatcher.group(1));
            return new String[]{"Windows", version};
        }

        // macOS 체크
        Matcher macMatcher = OS_MAC.matcher(userAgent);
        if (macMatcher.find()) {
            String version = macMatcher.group(1).replace("_", ".");
            return new String[]{"macOS", version};
        }

        // Linux 체크
        if (OS_LINUX.matcher(userAgent).find()) {
            return new String[]{"Linux", null};
        }

        return new String[]{"Unknown", null};
    }

    /**
     * Windows NT 버전을 친숙한 이름으로 변환
     */
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

    /**
     * 디바이스 타입 파싱
     */
    private QrGenVisitorDeviceType parseDeviceType(String userAgent) {
        if (userAgent == null) {
            return QrGenVisitorDeviceType.UNKNOWN;
        }

        String ua = userAgent.toLowerCase();

        // 봇 체크
        if (ua.contains("bot") || ua.contains("crawler") || ua.contains("spider") ||
                ua.contains("googlebot") || ua.contains("bingbot") || ua.contains("yandex")) {
            return QrGenVisitorDeviceType.BOT;
        }

        // 태블릿 체크 (모바일보다 먼저)
        if (ua.contains("ipad") || ua.contains("tablet") ||
                (ua.contains("android") && !ua.contains("mobile"))) {
            return QrGenVisitorDeviceType.TABLET;
        }

        // 모바일 체크
        if (ua.contains("mobile") || ua.contains("iphone") || ua.contains("ipod") ||
                ua.contains("android") || ua.contains("blackberry") || ua.contains("windows phone")) {
            return QrGenVisitorDeviceType.MOBILE;
        }

        // 데스크톱 (위 조건에 해당하지 않으면)
        if (ua.contains("windows") || ua.contains("macintosh") || ua.contains("linux")) {
            return QrGenVisitorDeviceType.DESKTOP;
        }

        return QrGenVisitorDeviceType.UNKNOWN;
    }
}
