package co.grap.pack.grap.external.api.service;

import co.grap.pack.grap.admin.sync.service.CmsSyncManager;
import co.grap.pack.grap.external.api.mapper.CmsExternalFestivalMapper;
import co.grap.pack.grap.external.api.model.CmsExternalFestival;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 제주 축제/행사 외부 API 서비스
 * API: https://www.jeju.go.kr/api/jejutoseoul/festival/
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CmsJejuFestivalApiService {

    private static final String JEJU_FESTIVAL_API_URL = "https://www.jeju.go.kr/api/jejutoseoul/festival/";

    private final RestTemplate restTemplate;
    private final CmsExternalFestivalMapper festivalMapper;
    private final ObjectMapper objectMapper;

    /**
     * 외부 API에서 축제/행사 데이터를 가져옵니다.
     */
    public Map<String, Object> fetchFestivalDataFromApi() {
        try {
            log.info("제주 축제/행사 API 호출 시작: {}", JEJU_FESTIVAL_API_URL);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(JEJU_FESTIVAL_API_URL, Map.class);

            if (response == null) {
                throw new RuntimeException("API 응답이 null입니다.");
            }

            log.info("제주 축제/행사 API 호출 성공: {} 건", response.get("total"));
            return response;

        } catch (Exception e) {
            log.error("제주 축제/행사 API 호출 실패", e);
            throw new RuntimeException("축제/행사 API 호출 실패: " + e.getMessage(), e);
        }
    }

    /**
     * API 응답 데이터를 Entity로 변환합니다.
     */
    @SuppressWarnings("unchecked")
    public List<CmsExternalFestival> transformApiDataToEntity(Map<String, Object> apiResponse) {
        List<CmsExternalFestival> festivals = new ArrayList<>();
        LocalDateTime fetchedAt = LocalDateTime.now();

        try {
            List<Map<String, Object>> items = (List<Map<String, Object>>) apiResponse.get("items");

            if (items == null || items.isEmpty()) {
                log.warn("API 응답에 items가 없거나 비어있습니다.");
                return festivals;
            }

            for (Map<String, Object> item : items) {
                try {
                    CmsExternalFestival festival = CmsExternalFestival.builder()
                            .originalApiId(getString(item, "seq"))
                            .title(getString(item, "title"))
                            .contentHtml(getString(item, "contents"))
                            .sourceUrl(getString(item, "url"))
                            .writerName(getString(item, "writer"))
                            .writtenDate(parseDate(getString(item, "writeDate")))
                            .filesInfo(objectMapper.writeValueAsString(item.get("files")))
                            .apiRawData(objectMapper.writeValueAsString(item))
                            .isShow(false) // 기본값: 노출하지 않음
                            .fetchedAt(fetchedAt)
                            .build();

                    festivals.add(festival);
                } catch (Exception e) {
                    log.error("축제 데이터 변환 실패: {}", item, e);
                }
            }

            log.info("축제 데이터 변환 완료: {} 건", festivals.size());
            return festivals;

        } catch (Exception e) {
            log.error("축제 데이터 변환 중 오류 발생", e);
            throw new RuntimeException("축제 데이터 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 축제/행사 데이터를 DB에 저장합니다 (upsert).
     */
    @Transactional
    public int saveFestivals(List<CmsExternalFestival> festivals) {
        int count = 0;

        for (CmsExternalFestival festival : festivals) {
            try {
                festivalMapper.upsert(festival);
                count++;
            } catch (Exception e) {
                log.error("축제 저장 실패: {}", festival.getOriginalApiId(), e);
            }
        }

        log.info("축제 데이터 저장 완료: {} 건", count);
        return count;
    }

    /**
     * 외부 API 동기화 전체 프로세스를 실행합니다.
     */
    @Transactional
    public void syncFestivalsFromExternalApi() {
        syncFestivalsFromExternalApi(null, null);
    }

    /**
     * 외부 API 동기화 전체 프로세스를 실행합니다 (중단 체크 포함).
     */
    @Transactional
    public void syncFestivalsFromExternalApi(String sessionId, CmsSyncManager syncManager) {
        try {
            log.info("=== 축제/행사 데이터 동기화 시작 ===");

            // 중단 체크
            if (syncManager != null && sessionId != null) {
                syncManager.checkCancellation(sessionId);
            }

            // 1. API에서 데이터 가져오기
            Map<String, Object> apiResponse = fetchFestivalDataFromApi();

            // 중단 체크
            if (syncManager != null && sessionId != null) {
                syncManager.checkCancellation(sessionId);
            }

            // 2. Entity로 변환
            List<CmsExternalFestival> festivals = transformApiDataToEntity(apiResponse);

            // 중단 체크
            if (syncManager != null && sessionId != null) {
                syncManager.checkCancellation(sessionId);
            }

            // 3. DB에 저장
            int savedCount = saveFestivals(festivals);

            log.info("=== 축제/행사 데이터 동기화 완료: {} 건 ===", savedCount);

        } catch (Exception e) {
            log.error("축제/행사 데이터 동기화 실패", e);
            throw new RuntimeException("축제/행사 데이터 동기화 실패: " + e.getMessage(), e);
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
     * 날짜 문자열을 LocalDateTime으로 변환합니다.
     */
    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        dateStr = dateStr.trim();

        // 여러 날짜 형식을 시도
        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),  // "2025-06-09 10:05:00"
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),     // "2025-06-09 10:05"
                DateTimeFormatter.ofPattern("yyyy-MM-dd")            // "2025-06-09"
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                // 날짜만 있는 경우 시간을 00:00:00으로 설정
                if (formatter.toString().contains("HH:mm")) {
                    return LocalDateTime.parse(dateStr, formatter);
                } else {
                    // "yyyy-MM-dd" 형식인 경우
                    return LocalDateTime.parse(dateStr + " 00:00:00",
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
            } catch (Exception e) {
                // 다음 포맷 시도
            }
        }

        log.warn("날짜 파싱 실패 (모든 형식 시도): {}", dateStr);
        return null;
    }
}
