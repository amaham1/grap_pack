package com.example.cms.external.api.service;

import com.example.cms.admin.sync.service.SyncManager;
import com.example.cms.external.api.mapper.ExhibitionMapper;
import com.example.cms.external.api.model.Exhibition;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 제주 공연/전시 외부 API 서비스
 * API: http://www.jeju.go.kr/rest/JejuExhibitionService/getJejucultureExhibitionList
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JejuExhibitionApiService {

    private static final String JEJU_EXHIBITION_API_URL =
            "http://www.jeju.go.kr/rest/JejuExhibitionService/getJejucultureExhibitionList";

    private final RestTemplate restTemplate;
    private final ExhibitionMapper exhibitionMapper;
    private final ObjectMapper objectMapper;

    /**
     * 외부 API에서 공연/전시 데이터를 가져옵니다 (페이징).
     */
    public List<Map<String, Object>> fetchAllExhibitionDataFromApi() {
        return fetchAllExhibitionDataFromApi(null, null);
    }

    /**
     * 외부 API에서 공연/전시 데이터를 가져옵니다 (페이징, 중단 체크 포함).
     */
    public List<Map<String, Object>> fetchAllExhibitionDataFromApi(String sessionId, SyncManager syncManager) {
        List<Map<String, Object>> allItems = new ArrayList<>();
        int page = 1;
        int pageSize = 500;

        try {
            log.info("제주 공연/전시 API 호출 시작");

            while (true) {
                // 중단 체크
                if (syncManager != null && sessionId != null) {
                    syncManager.checkCancellation(sessionId);
                }

                String url = String.format("%s?page=%d&pageSize=%d", JEJU_EXHIBITION_API_URL, page, pageSize);

                @SuppressWarnings("unchecked")
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);

                if (response == null) {
                    break;
                }

                String resultCode = getString(response, "resultCode");
                if (!"00".equals(resultCode)) {
                    log.warn("API 호출 실패: {}", response.get("resultMsg"));
                    break;
                }

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

                if (items == null || items.isEmpty()) {
                    break;
                }

                allItems.addAll(items);
                log.info("페이지 {} 수집 완료: {} 건", page, items.size());

                page++;

                // API 부하 방지를 위한 딜레이
                Thread.sleep(500);
            }

            log.info("제주 공연/전시 API 전체 수집 완료: {} 건", allItems.size());
            return allItems;

        } catch (Exception e) {
            log.error("제주 공연/전시 API 호출 실패", e);
            throw new RuntimeException("공연/전시 API 호출 실패: " + e.getMessage(), e);
        }
    }

    /**
     * API 응답 데이터를 Entity로 변환합니다.
     */
    public List<Exhibition> transformApiDataToEntity(List<Map<String, Object>> items) {
        List<Exhibition> exhibitions = new ArrayList<>();
        LocalDateTime fetchedAt = LocalDateTime.now();

        for (Map<String, Object> item : items) {
            try {
                Exhibition exhibition = Exhibition.builder()
                        .originalApiId(getString(item, "seq"))
                        .title(getString(item, "title"))
                        .categoryName(getString(item, "categoryName"))
                        .coverImageUrl(getString(item, "cover"))
                        .startDate(parseTimestamp(item.get("start")))
                        .endDate(parseTimestamp(item.get("end")))
                        .timeInfo(getString(item, "hour"))
                        .locationName(getString(item, "locNames"))
                        .organizerInfo(getString(item, "owner"))
                        .telNumber(getString(item, "tel"))
                        .payInfo(getString(item, "pay"))
                        .statusInfo(getString(item, "stat"))
                        .divisionName(getString(item, "divName"))
                        .apiRawData(objectMapper.writeValueAsString(item))
                        .isShow(false)
                        .fetchedAt(fetchedAt)
                        .build();

                exhibitions.add(exhibition);
            } catch (Exception e) {
                log.error("공연/전시 데이터 변환 실패: {}", item, e);
            }
        }

        log.info("공연/전시 데이터 변환 완료: {} 건", exhibitions.size());
        return exhibitions;
    }

    /**
     * 공연/전시 데이터를 DB에 저장합니다 (upsert).
     */
    @Transactional
    public int saveExhibitions(List<Exhibition> exhibitions) {
        int count = 0;

        for (Exhibition exhibition : exhibitions) {
            try {
                exhibitionMapper.upsert(exhibition);
                count++;
            } catch (Exception e) {
                log.error("공연/전시 저장 실패: {}", exhibition.getOriginalApiId(), e);
            }
        }

        log.info("공연/전시 데이터 저장 완료: {} 건", count);
        return count;
    }

    /**
     * 외부 API 동기화 전체 프로세스를 실행합니다.
     */
    @Transactional
    public void syncExhibitionsFromExternalApi() {
        syncExhibitionsFromExternalApi(null, null);
    }

    /**
     * 외부 API 동기화 전체 프로세스를 실행합니다 (중단 체크 포함).
     */
    @Transactional
    public void syncExhibitionsFromExternalApi(String sessionId, SyncManager syncManager) {
        try {
            log.info("=== 공연/전시 데이터 동기화 시작 ===");

            // 중단 체크
            if (syncManager != null && sessionId != null) {
                syncManager.checkCancellation(sessionId);
            }

            // 1. API에서 모든 페이지 데이터 가져오기 (중단 체크 포함)
            List<Map<String, Object>> items = fetchAllExhibitionDataFromApi(sessionId, syncManager);

            // 중단 체크
            if (syncManager != null && sessionId != null) {
                syncManager.checkCancellation(sessionId);
            }

            // 2. Entity로 변환
            List<Exhibition> exhibitions = transformApiDataToEntity(items);

            // 중단 체크
            if (syncManager != null && sessionId != null) {
                syncManager.checkCancellation(sessionId);
            }

            // 3. DB에 저장
            int savedCount = saveExhibitions(exhibitions);

            log.info("=== 공연/전시 데이터 동기화 완료: {} 건 ===", savedCount);

        } catch (Exception e) {
            log.error("공연/전시 데이터 동기화 실패", e);
            throw new RuntimeException("공연/전시 데이터 동기화 실패: " + e.getMessage(), e);
        }
    }

    /**
     * Map에서 String 값을 가져옵니다.
     */
    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Timestamp를 LocalDateTime으로 변환합니다.
     */
    private LocalDateTime parseTimestamp(Object timestamp) {
        if (timestamp == null) {
            return null;
        }

        try {
            long timestampLong = Long.parseLong(timestamp.toString());
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampLong), ZoneId.systemDefault());
        } catch (Exception e) {
            log.warn("Timestamp 파싱 실패: {}", timestamp, e);
            return null;
        }
    }
}
