package co.grap.pack.qrgen.generator.service;

import co.grap.pack.qrgen.generator.mapper.QrGenHistoryMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * QR 코드 생성 횟수 제한 서비스
 * - 로그인 사용자: DB 기반 일일 100개 제한
 * - 비로그인 사용자: 메모리(IP) 기반 일일 10개 제한
 * - 미리보기: IP 기반 분당 60회 제한
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QrGenRateLimitService {

    private final QrGenHistoryMapper qrGenHistoryMapper;

    /** 로그인 사용자 일일 제한 */
    private static final int DAILY_LIMIT_AUTHENTICATED = 100;

    /** 비로그인 사용자 일일 제한 */
    private static final int DAILY_LIMIT_ANONYMOUS = 10;

    /** 미리보기 분당 제한 */
    private static final int PREVIEW_LIMIT_PER_MINUTE = 60;

    /** 비로그인 사용자 IP별 생성 카운트 (자정 초기화) */
    private final ConcurrentHashMap<String, AtomicInteger> anonymousCountMap = new ConcurrentHashMap<>();

    /** 미리보기 IP별 카운트 (1분마다 초기화) */
    private final ConcurrentHashMap<String, AtomicInteger> previewCountMap = new ConcurrentHashMap<>();

    /**
     * Rate Limit 체크 결과
     */
    public record QrGenRateLimitCheckResult(boolean exceeded, int remaining, int limit, String message) {}

    /**
     * 로그인 사용자 Rate Limit 체크
     */
    public QrGenRateLimitCheckResult checkQrGenAuthenticatedRateLimit(Long userId) {
        int todayCount = qrGenHistoryMapper.countQrGenHistoryTodayByUserId(userId);
        boolean exceeded = todayCount >= DAILY_LIMIT_AUTHENTICATED;
        int remaining = Math.max(0, DAILY_LIMIT_AUTHENTICATED - todayCount);
        String message = exceeded
                ? "일일 QR 생성 한도(" + DAILY_LIMIT_AUTHENTICATED + "개)를 초과했습니다."
                : null;
        log.info("✅ [CHECK] 로그인 사용자 일일 QR 생성 카운트: userId={}, count={}/{}", userId, todayCount, DAILY_LIMIT_AUTHENTICATED);
        return new QrGenRateLimitCheckResult(exceeded, remaining, DAILY_LIMIT_AUTHENTICATED, message);
    }

    /**
     * 비로그인 사용자 Rate Limit 체크
     */
    public QrGenRateLimitCheckResult checkQrGenAnonymousRateLimit(String ipAddress) {
        int count = getAnonymousCount(ipAddress);
        boolean exceeded = count >= DAILY_LIMIT_ANONYMOUS;
        int remaining = Math.max(0, DAILY_LIMIT_ANONYMOUS - count);
        String message = exceeded
                ? "일일 QR 생성 한도(" + DAILY_LIMIT_ANONYMOUS + "개)를 초과했습니다. 로그인하시면 더 많이 생성할 수 있습니다."
                : null;
        log.info("✅ [CHECK] 비로그인 사용자 일일 QR 생성 카운트: ip={}, count={}/{}", ipAddress, count, DAILY_LIMIT_ANONYMOUS);
        return new QrGenRateLimitCheckResult(exceeded, remaining, DAILY_LIMIT_ANONYMOUS, message);
    }

    /**
     * 미리보기 Rate Limit 체크 (IP 기반 분당 제한)
     */
    public boolean isQrGenPreviewRateLimitExceeded(String ipAddress) {
        AtomicInteger counter = previewCountMap.computeIfAbsent(ipAddress, k -> new AtomicInteger(0));
        return counter.incrementAndGet() > PREVIEW_LIMIT_PER_MINUTE;
    }

    /**
     * 비로그인 사용자 생성 카운트 증가
     */
    public void incrementQrGenAnonymousCount(String ipAddress) {
        anonymousCountMap.computeIfAbsent(ipAddress, k -> new AtomicInteger(0)).incrementAndGet();
    }

    /**
     * 클라이언트 IP 주소 추출
     */
    public String getClientIpAddress(HttpServletRequest request) {
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
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * 매일 자정에 비로그인 사용자 카운트 초기화
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void resetQrGenAnonymousRateLimitDaily() {
        int size = anonymousCountMap.size();
        anonymousCountMap.clear();
        log.info("✅ [CHECK] 비로그인 사용자 QR 생성 카운트 자정 초기화 완료: 초기화된 IP 수={}", size);
    }

    /**
     * 1분마다 미리보기 카운트 초기화
     */
    @Scheduled(fixedRate = 60000)
    public void resetQrGenPreviewRateLimit() {
        previewCountMap.clear();
    }

    private int getAnonymousCount(String ipAddress) {
        AtomicInteger counter = anonymousCountMap.get(ipAddress);
        return counter != null ? counter.get() : 0;
    }
}
