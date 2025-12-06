package co.grap.pack.external.api.scheduler;

import co.grap.pack.external.api.service.JejuExhibitionApiService;
import co.grap.pack.external.api.service.JejuFestivalApiService;
import co.grap.pack.external.api.service.JejuGasPriceApiService;
import co.grap.pack.external.api.service.JejuWelfareApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 제주도 외부 API 데이터 동기화 스케줄러
 *
 * 실행 주기: 새벽 2시 5분, 8시 5분, 14시 5분, 20시 5분 (하루 4회)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalApiScheduler {

    private final JejuFestivalApiService festivalService;
    private final JejuExhibitionApiService exhibitionService;
    private final JejuWelfareApiService welfareService;
    private final JejuGasPriceApiService gasPriceService;

    // 중복 실행 방지용 플래그
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * 통합 데이터 동기화 스케줄러
     * 크론 표현식: "초 분 시 일 월 요일"
     * "0 5 2,8,14,20 * * *" = 매일 2시 5분, 8시 5분, 14시 5분, 20시 5분에 실행
     */
    @Scheduled(cron = "0 5 2,8,14,20 * * *")
    public void syncAllExternalApiData() {
        // 이미 실행 중이면 건너뛰기
        if (!isRunning.compareAndSet(false, true)) {
            log.warn("이전 동기화 작업이 아직 실행 중입니다. 이번 실행을 건너뜁니다.");
            return;
        }

        try {
            String startTime = getCurrentTimeString();
            log.info("========================================");
            log.info("외부 API 통합 데이터 동기화 시작: {}", startTime);
            log.info("========================================");

            long startMillis = System.currentTimeMillis();

            // 각 서비스별 동기화 실행
            SyncResult festivalResult = syncFestivals();
            SyncResult exhibitionResult = syncExhibitions();
            SyncResult welfareResult = syncWelfareServices();
            SyncResult gasPriceResult = syncGasPrices();

            long endMillis = System.currentTimeMillis();
            long durationSeconds = (endMillis - startMillis) / 1000;

            // 결과 요약 로깅
            logSyncSummary(festivalResult, exhibitionResult, welfareResult, gasPriceResult, durationSeconds);

            String endTime = getCurrentTimeString();
            log.info("========================================");
            log.info("외부 API 통합 데이터 동기화 완료: {}", endTime);
            log.info("========================================");

        } catch (Exception e) {
            log.error("외부 API 통합 데이터 동기화 중 예상치 못한 오류 발생", e);
        } finally {
            isRunning.set(false);
        }
    }

    /**
     * 축제/행사 데이터 동기화
     */
    private SyncResult syncFestivals() {
        String serviceName = "축제/행사";
        try {
            log.info("--- {} 데이터 동기화 시작 ---", serviceName);
            long startMillis = System.currentTimeMillis();

            festivalService.syncFestivalsFromExternalApi();

            long duration = (System.currentTimeMillis() - startMillis) / 1000;
            log.info("--- {} 데이터 동기화 완료 ({}초) ---", serviceName, duration);

            return SyncResult.success(serviceName, duration);

        } catch (Exception e) {
            log.error("--- {} 데이터 동기화 실패 ---", serviceName, e);
            return SyncResult.failure(serviceName, e.getMessage());
        }
    }

    /**
     * 공연/전시 데이터 동기화
     */
    private SyncResult syncExhibitions() {
        String serviceName = "공연/전시";
        try {
            log.info("--- {} 데이터 동기화 시작 ---", serviceName);
            long startMillis = System.currentTimeMillis();

            exhibitionService.syncExhibitionsFromExternalApi();

            long duration = (System.currentTimeMillis() - startMillis) / 1000;
            log.info("--- {} 데이터 동기화 완료 ({}초) ---", serviceName, duration);

            return SyncResult.success(serviceName, duration);

        } catch (Exception e) {
            log.error("--- {} 데이터 동기화 실패 ---", serviceName, e);
            return SyncResult.failure(serviceName, e.getMessage());
        }
    }

    /**
     * 복지서비스 데이터 동기화
     */
    private SyncResult syncWelfareServices() {
        String serviceName = "복지서비스";
        try {
            log.info("--- {} 데이터 동기화 시작 ---", serviceName);
            long startMillis = System.currentTimeMillis();

            welfareService.syncWelfareServicesFromExternalApi();

            long duration = (System.currentTimeMillis() - startMillis) / 1000;
            log.info("--- {} 데이터 동기화 완료 ({}초) ---", serviceName, duration);

            return SyncResult.success(serviceName, duration);

        } catch (Exception e) {
            log.error("--- {} 데이터 동기화 실패 ---", serviceName, e);
            return SyncResult.failure(serviceName, e.getMessage());
        }
    }

    /**
     * 주유소 가격 데이터 동기화
     */
    private SyncResult syncGasPrices() {
        String serviceName = "주유소 가격";
        try {
            log.info("--- {} 데이터 동기화 시작 ---", serviceName);
            long startMillis = System.currentTimeMillis();

            gasPriceService.syncGasPricesFromExternalApi();

            long duration = (System.currentTimeMillis() - startMillis) / 1000;
            log.info("--- {} 데이터 동기화 완료 ({}초) ---", serviceName, duration);

            return SyncResult.success(serviceName, duration);

        } catch (Exception e) {
            log.error("--- {} 데이터 동기화 실패 ---", serviceName, e);
            return SyncResult.failure(serviceName, e.getMessage());
        }
    }

    /**
     * 동기화 결과 요약을 로깅합니다.
     */
    private void logSyncSummary(SyncResult festival, SyncResult exhibition,
                                SyncResult welfare, SyncResult gasPrice,
                                long totalDuration) {
        log.info("");
        log.info("========================================");
        log.info("동기화 결과 요약");
        log.info("========================================");

        logSyncResultDetail(festival);
        logSyncResultDetail(exhibition);
        logSyncResultDetail(welfare);
        logSyncResultDetail(gasPrice);

        log.info("----------------------------------------");
        log.info("전체 소요 시간: {}초", totalDuration);

        long successCount = countSuccess(festival, exhibition, welfare, gasPrice);
        long failureCount = 4 - successCount;

        log.info("성공: {} / 실패: {}", successCount, failureCount);
        log.info("========================================");
        log.info("");
    }

    /**
     * 개별 동기화 결과를 로깅합니다.
     */
    private void logSyncResultDetail(SyncResult result) {
        if (result.isSuccess()) {
            log.info("[성공] {}: {}초 소요", result.getServiceName(), result.getDuration());
        } else {
            log.error("[실패] {}: {}", result.getServiceName(), result.getErrorMessage());
        }
    }

    /**
     * 성공한 작업 수를 카운트합니다.
     */
    private long countSuccess(SyncResult... results) {
        long count = 0;
        for (SyncResult result : results) {
            if (result.isSuccess()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 현재 시간을 문자열로 반환합니다.
     */
    private String getCurrentTimeString() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 동기화 결과를 저장하는 내부 클래스
     */
    private static class SyncResult {
        private final String serviceName;
        private final boolean success;
        private final long duration;
        private final String errorMessage;

        private SyncResult(String serviceName, boolean success, long duration, String errorMessage) {
            this.serviceName = serviceName;
            this.success = success;
            this.duration = duration;
            this.errorMessage = errorMessage;
        }

        public static SyncResult success(String serviceName, long duration) {
            return new SyncResult(serviceName, true, duration, null);
        }

        public static SyncResult failure(String serviceName, String errorMessage) {
            return new SyncResult(serviceName, false, 0, errorMessage);
        }

        public String getServiceName() {
            return serviceName;
        }

        public boolean isSuccess() {
            return success;
        }

        public long getDuration() {
            return duration;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
