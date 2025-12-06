package co.grap.pack.grap.external.api.service;

import co.grap.pack.grap.admin.sync.service.SyncManager;
import co.grap.pack.grap.external.api.mapper.ExternalWelfareServiceMapper;
import co.grap.pack.grap.external.api.model.ExternalWelfareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 제주 복지서비스 외부 API 서비스
 * API: http://www.jeju.go.kr/rest/JejuWelfareServiceInfo/getJejuWelfareServiceInfoList
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JejuWelfareApiService {

    private static final String JEJU_WELFARE_API_URL =
            "http://www.jeju.go.kr/rest/JejuWelfareServiceInfo/getJejuWelfareServiceInfoList";

    private final RestTemplate restTemplate;
    private final ExternalWelfareServiceMapper welfareServiceMapper;
    private final ObjectMapper objectMapper;

    /**
     * 외부 API에서 복지서비스 데이터를 가져옵니다.
     */
    public Map<String, Object> fetchWelfareDataFromApi() {
        try {
            log.info("제주 복지서비스 API 호출 시작: {}", JEJU_WELFARE_API_URL);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(JEJU_WELFARE_API_URL, Map.class);

            if (response == null) {
                throw new RuntimeException("API 응답이 null입니다.");
            }

            String resultCode = getString(response, "resultCode");
            if (!"00".equals(resultCode)) {
                throw new RuntimeException("API 호출 실패: " + response.get("resultMsg"));
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> list = (List<Map<String, Object>>) response.get("list");
            log.info("제주 복지서비스 API 호출 성공: {} 건", list != null ? list.size() : 0);

            return response;

        } catch (Exception e) {
            log.error("제주 복지서비스 API 호출 실패", e);
            throw new RuntimeException("복지서비스 API 호출 실패: " + e.getMessage(), e);
        }
    }

    /**
     * API 응답 데이터를 Entity로 변환합니다.
     */
    @SuppressWarnings("unchecked")
    public List<ExternalWelfareService> transformApiDataToEntity(Map<String, Object> apiResponse) {
        List<ExternalWelfareService> services = new ArrayList<>();
        LocalDateTime fetchedAt = LocalDateTime.now();

        try {
            List<Map<String, Object>> list = (List<Map<String, Object>>) apiResponse.get("list");

            if (list == null || list.isEmpty()) {
                log.warn("API 응답에 list가 없거나 비어있습니다.");
                return services;
            }

            for (Map<String, Object> item : list) {
                try {
                    ExternalWelfareService service = ExternalWelfareService.builder()
                            .originalApiId(getString(item, "seq"))
                            .serviceName(getString(item, "name"))
                            .isAllLocation(getBoolean(item, "allLoc"))
                            .isJejuLocation(getBoolean(item, "jejuLoc"))
                            .isSeogwipoLocation(getBoolean(item, "seogwipoLoc"))
                            .supportTargetHtml(getString(item, "support"))
                            .supportContentHtml(getString(item, "contents"))
                            .applicationInfoHtml(getString(item, "apply"))
                            .apiRawData(objectMapper.writeValueAsString(item))
                            .isShow(false)
                            .fetchedAt(fetchedAt)
                            .build();

                    services.add(service);
                } catch (Exception e) {
                    log.error("복지서비스 데이터 변환 실패: {}", item, e);
                }
            }

            log.info("복지서비스 데이터 변환 완료: {} 건", services.size());
            return services;

        } catch (Exception e) {
            log.error("복지서비스 데이터 변환 중 오류 발생", e);
            throw new RuntimeException("복지서비스 데이터 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 복지서비스 데이터를 DB에 저장합니다 (upsert).
     */
    @Transactional
    public int saveWelfareServices(List<ExternalWelfareService> services) {
        int count = 0;

        for (ExternalWelfareService service : services) {
            try {
                welfareServiceMapper.upsert(service);
                count++;
            } catch (Exception e) {
                log.error("복지서비스 저장 실패: {}", service.getOriginalApiId(), e);
            }
        }

        log.info("복지서비스 데이터 저장 완료: {} 건", count);
        return count;
    }

    /**
     * 외부 API 동기화 전체 프로세스를 실행합니다.
     */
    @Transactional
    public void syncWelfareServicesFromExternalApi() {
        syncWelfareServicesFromExternalApi(null, null);
    }

    /**
     * 외부 API 동기화 전체 프로세스를 실행합니다 (중단 체크 포함).
     */
    @Transactional
    public void syncWelfareServicesFromExternalApi(String sessionId, SyncManager syncManager) {
        try {
            log.info("=== 복지서비스 데이터 동기화 시작 ===");

            // 중단 체크
            if (syncManager != null && sessionId != null) {
                syncManager.checkCancellation(sessionId);
            }

            // 1. API에서 데이터 가져오기
            Map<String, Object> apiResponse = fetchWelfareDataFromApi();

            // 중단 체크
            if (syncManager != null && sessionId != null) {
                syncManager.checkCancellation(sessionId);
            }

            // 2. Entity로 변환
            List<ExternalWelfareService> services = transformApiDataToEntity(apiResponse);

            // 중단 체크
            if (syncManager != null && sessionId != null) {
                syncManager.checkCancellation(sessionId);
            }

            // 3. DB에 저장
            int savedCount = saveWelfareServices(services);

            log.info("=== 복지서비스 데이터 동기화 완료: {} 건 ===", savedCount);

        } catch (Exception e) {
            log.error("복지서비스 데이터 동기화 실패", e);
            throw new RuntimeException("복지서비스 데이터 동기화 실패: " + e.getMessage(), e);
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
     * Map에서 Boolean 값을 가져옵니다.
     */
    private Boolean getBoolean(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(value.toString());
    }
}
