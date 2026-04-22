package co.grap.pack.grap.external.api.service;

import co.grap.pack.grap.admin.sync.service.CmsSyncManager;
import co.grap.pack.grap.external.api.mapper.CmsExternalRealEstateMapper;
import co.grap.pack.grap.realestate.model.RealEstateSyncCheckpoint;
import co.grap.pack.grap.realestate.model.RealEstateTransactionRecord;
import co.grap.pack.grap.realestate.support.RealEstateApiProperties;
import co.grap.pack.grap.realestate.support.RealEstateDataset;
import co.grap.pack.grap.realestate.support.RealEstatePublicApiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 제주 부동산 실거래 공공데이터 동기화 서비스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CmsJejuRealEstateApiService {

    private static final int NUM_OF_ROWS = 500;
    private static final int MAX_FETCH_ATTEMPTS = 3;
    private static final long FETCH_RETRY_DELAY_MILLIS = 2000L;
    private static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyyMM");
    private static final List<RealEstateRegion> JEJU_REGIONS = List.of(
            new RealEstateRegion("50110", "제주시"),
            new RealEstateRegion("50130", "서귀포시")
    );

    private final RestTemplate restTemplate;
    private final CmsExternalRealEstateMapper externalRealEstateMapper;
    private final RealEstatePublicApiParser publicApiParser;
    private final RealEstateApiProperties apiProperties;

    public void syncRecentMonths() {
        syncRecentMonths(null, null);
    }

    public void syncRecentMonths(String sessionId, CmsSyncManager syncManager) {
        ensureServiceKey();
        List<String> targetMonths = createRecentYearMonths(currentYearMonth(), apiProperties.getRecentMonths());

        for (String yearMonth : targetMonths) {
            for (RealEstateRegion region : JEJU_REGIONS) {
                for (RealEstateDataset dataset : RealEstateDataset.values()) {
                    checkCancellation(sessionId, syncManager);
                    int syncedCount = syncMonth(dataset, region, yearMonth);
                    log.info(
                            "부동산 최근월 동기화 완료: dataset={}, lawdCode={}, yearMonth={}, count={}",
                            dataset.getDatasetId(),
                            region.lawdCode(),
                            yearMonth,
                            syncedCount
                    );
                }
            }
        }
    }

    public void bootstrapAllHistory() {
        bootstrapAllHistory(null, null);
    }

    public void bootstrapAllHistory(String sessionId, CmsSyncManager syncManager) {
        ensureServiceKey();
        String currentYearMonth = currentYearMonth();

        for (RealEstateRegion region : JEJU_REGIONS) {
            for (RealEstateDataset dataset : RealEstateDataset.values()) {
                checkCancellation(sessionId, syncManager);
                bootstrapDataset(dataset, region, currentYearMonth, sessionId, syncManager);
            }
        }
    }

    private void bootstrapDataset(
            RealEstateDataset dataset,
            RealEstateRegion region,
            String currentYearMonth,
            String sessionId,
            CmsSyncManager syncManager
    ) {
        RealEstateSyncCheckpoint checkpoint = externalRealEstateMapper.selectSyncCheckpoint(dataset.getDatasetId(), region.lawdCode());
        String startYearMonth = resolveBootstrapStartYearMonth(dataset, checkpoint);
        String lastSyncedYearMonth = checkpoint == null ? "" : defaultString(checkpoint.getLastSyncedYearMonth());
        LocalDateTime now = LocalDateTime.now();

        externalRealEstateMapper.startCheckpoint(
                dataset.getDatasetId(),
                region.lawdCode(),
                now,
                "전체 적재 시작",
                now
        );

        if (compareYearMonth(startYearMonth, currentYearMonth) > 0) {
            externalRealEstateMapper.completeCheckpoint(
                    dataset.getDatasetId(),
                    region.lawdCode(),
                    defaultString(checkpoint == null ? null : checkpoint.getLastSyncedYearMonth()),
                    now,
                    "이미 최신 월까지 적재되어 있습니다.",
                    now
            );
            return;
        }

        try {
            for (String yearMonth : createYearMonthRange(startYearMonth, currentYearMonth)) {
                checkCancellation(sessionId, syncManager);
                int syncedCount = syncMonth(dataset, region, yearMonth);
                lastSyncedYearMonth = yearMonth;
                externalRealEstateMapper.updateCheckpointProgress(
                        dataset.getDatasetId(),
                        region.lawdCode(),
                        lastSyncedYearMonth,
                        yearMonth + " 적재 완료 (" + syncedCount + "건)",
                        LocalDateTime.now()
                );
            }

            externalRealEstateMapper.completeCheckpoint(
                    dataset.getDatasetId(),
                    region.lawdCode(),
                    lastSyncedYearMonth,
                    LocalDateTime.now(),
                    "전체 적재 완료",
                    LocalDateTime.now()
            );
        } catch (RuntimeException exception) {
            externalRealEstateMapper.updateCheckpointProgress(
                    dataset.getDatasetId(),
                    region.lawdCode(),
                    lastSyncedYearMonth,
                    "실패: " + exception.getMessage(),
                    LocalDateTime.now()
            );
            throw exception;
        }
    }

    private int syncMonth(RealEstateDataset dataset, RealEstateRegion region, String yearMonth) {
        RealEstatePublicApiParser.ParsedPage firstPage = fetchPage(dataset, region, yearMonth, 1);
        int savedCount = persistItems(firstPage.items());
        int totalPages = Math.max(1, (int) Math.ceil((double) firstPage.totalCount() / NUM_OF_ROWS));

        for (int pageNo = 2; pageNo <= totalPages; pageNo += 1) {
            RealEstatePublicApiParser.ParsedPage page = fetchPage(dataset, region, yearMonth, pageNo);
            savedCount += persistItems(page.items());
        }

        return savedCount;
    }

    private RealEstatePublicApiParser.ParsedPage fetchPage(
            RealEstateDataset dataset,
            RealEstateRegion region,
            String yearMonth,
            int pageNo
    ) {
        for (int attempt = 1; attempt <= MAX_FETCH_ATTEMPTS; attempt += 1) {
            try {
                String xml = restTemplate.getForObject(
                        apiProperties.buildUri(dataset, region.lawdCode(), yearMonth, pageNo, NUM_OF_ROWS),
                        String.class
                );
                return parseXml(xml, dataset, region);
            } catch (ResourceAccessException exception) {
                if (attempt >= MAX_FETCH_ATTEMPTS) {
                    throw new IllegalStateException(
                            String.format(
                                    "공공데이터 API 호출에 실패했습니다. dataset=%s, lawdCode=%s, yearMonth=%s, pageNo=%d",
                                    dataset.getDatasetId(),
                                    region.lawdCode(),
                                    yearMonth,
                                    pageNo
                            ),
                            exception
                    );
                }

                log.warn(
                        "부동산 공공데이터 API 재시도: dataset={}, lawdCode={}, yearMonth={}, pageNo={}, attempt={}/{}",
                        dataset.getDatasetId(),
                        region.lawdCode(),
                        yearMonth,
                        pageNo,
                        attempt,
                        MAX_FETCH_ATTEMPTS,
                        exception
                );
                sleepBeforeRetry();
            }
        }

        throw new IllegalStateException("부동산 공공데이터 API 호출 중 예기치 않은 상태가 발생했습니다.");
    }

    private RealEstatePublicApiParser.ParsedPage parseXml(
            String xml,
            RealEstateDataset dataset,
            RealEstateRegion region
    ) {
        if (xml == null || xml.isBlank()) {
            throw new IllegalStateException("공공데이터 API 응답이 비어 있습니다.");
        }

        String sanitizedXml = xml.stripLeading();
        if (!sanitizedXml.isEmpty() && sanitizedXml.charAt(0) == '\uFEFF') {
            sanitizedXml = sanitizedXml.substring(1);
        }
        if (!sanitizedXml.startsWith("<")) {
            throw new IllegalStateException(
                    "공공데이터 XML 응답이 아닙니다: "
                            + sanitizedXml.substring(0, Math.min(120, sanitizedXml.length())).replaceAll("\\s+", " ")
            );
        }

        return publicApiParser.parse(sanitizedXml, dataset, region.lawdCode(), region.sggName(), LocalDateTime.now());
    }

    private int persistItems(List<RealEstateTransactionRecord> items) {
        int count = 0;
        for (RealEstateTransactionRecord item : items) {
            externalRealEstateMapper.upsertRealEstateTransaction(item);
            count++;
        }
        return count;
    }

    private void ensureServiceKey() {
        if (apiProperties.getNormalizedServiceKey().isBlank()) {
            throw new IllegalStateException("GRAP_PUBLIC_DATA_SERVICE_KEY 환경변수가 설정되어 있지 않습니다.");
        }
    }

    private void checkCancellation(String sessionId, CmsSyncManager syncManager) {
        if (syncManager != null && sessionId != null) {
            syncManager.checkCancellation(sessionId);
        }
    }

    private String resolveBootstrapStartYearMonth(RealEstateDataset dataset, RealEstateSyncCheckpoint checkpoint) {
        if (checkpoint == null || defaultString(checkpoint.getLastSyncedYearMonth()).isBlank()) {
            return dataset.getStartYearMonth();
        }

        String nextYearMonth = incrementYearMonth(checkpoint.getLastSyncedYearMonth());
        if (compareYearMonth(nextYearMonth, dataset.getStartYearMonth()) < 0) {
            return dataset.getStartYearMonth();
        }
        return nextYearMonth;
    }

    private List<String> createRecentYearMonths(String currentYearMonth, int monthCount) {
        int safeCount = Math.max(1, monthCount);
        LocalDate current = LocalDate.parse(currentYearMonth + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
        List<String> months = new ArrayList<>();
        for (int index = safeCount - 1; index >= 0; index -= 1) {
            months.add(current.minusMonths(index).format(YEAR_MONTH_FORMAT));
        }
        return months;
    }

    private List<String> createYearMonthRange(String startYearMonth, String endYearMonth) {
        LocalDate start = LocalDate.parse(startYearMonth + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate end = LocalDate.parse(endYearMonth + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
        List<String> months = new ArrayList<>();
        LocalDate cursor = start;

        while (!cursor.isAfter(end)) {
            months.add(cursor.format(YEAR_MONTH_FORMAT));
            cursor = cursor.plusMonths(1);
        }
        return months;
    }

    private String incrementYearMonth(String yearMonth) {
        LocalDate current = LocalDate.parse(yearMonth + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
        return current.plusMonths(1).format(YEAR_MONTH_FORMAT);
    }

    private int compareYearMonth(String left, String right) {
        return defaultString(left).compareTo(defaultString(right));
    }

    private String currentYearMonth() {
        return LocalDate.now().format(YEAR_MONTH_FORMAT);
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private void sleepBeforeRetry() {
        try {
            Thread.sleep(FETCH_RETRY_DELAY_MILLIS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("부동산 공공데이터 API 재시도가 중단되었습니다.", exception);
        }
    }

    private record RealEstateRegion(String lawdCode, String sggName) {
    }
}
