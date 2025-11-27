package com.example.cms.external.api.service;

import com.example.cms.admin.sync.service.SyncManager;
import com.example.cms.external.api.mapper.ExternalGasPriceMapper;
import com.example.cms.external.api.mapper.ExternalGasStationMapper;
import com.example.cms.external.api.model.ExternalGasPrice;
import com.example.cms.external.api.model.ExternalGasStation;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 제주 주유소 가격 외부 API 서비스
 * API: http://api.jejuits.go.kr/api/infoGasPriceList
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JejuGasPriceApiService {

    private static final String JEJU_GAS_PRICE_API_URL = "http://api.jejuits.go.kr/api/infoGasPriceList";
    private static final String API_CODE = "860665";

    private final RestTemplate restTemplate;
    private final ExternalGasStationMapper gasStationMapper;
    private final ExternalGasPriceMapper gasPriceMapper;
    private final ObjectMapper objectMapper;

    /**
     * 외부 API에서 주유소 가격 데이터를 가져옵니다.
     */
    public Map<String, Object> fetchGasPriceDataFromApi() {
        try {
            String url = JEJU_GAS_PRICE_API_URL + "?code=" + API_CODE;
            log.info("제주 주유소 가격 API 호출 시작: {}", url);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null) {
                throw new RuntimeException("API 응답이 null입니다.");
            }

            String result = getString(response, "result");
            if (!"success".equals(result)) {
                throw new RuntimeException("API 호출 실패: " + result);
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> info = (List<Map<String, Object>>) response.get("info");
            log.info("제주 주유소 가격 API 호출 성공: {} 건", info != null ? info.size() : 0);

            return response;

        } catch (Exception e) {
            log.error("제주 주유소 가격 API 호출 실패", e);
            throw new RuntimeException("주유소 가격 API 호출 실패: " + e.getMessage(), e);
        }
    }

    /**
     * API 응답 데이터를 GasStation Entity로 변환합니다.
     */
    @SuppressWarnings("unchecked")
    public List<ExternalGasStation> transformApiDataToStationEntity(Map<String, Object> apiResponse) {
        List<ExternalGasStation> stations = new ArrayList<>();

        try {
            List<Map<String, Object>> info = (List<Map<String, Object>>) apiResponse.get("info");

            if (info == null || info.isEmpty()) {
                log.warn("API 응답에 info가 없거나 비어있습니다.");
                return stations;
            }

            for (Map<String, Object> item : info) {
                try {
                    String opinetId = getString(item, "id");
                    if (opinetId == null || opinetId.trim().isEmpty()) {
                        log.warn("opinet_id가 비어있는 데이터 건너뜀: {}", item);
                        continue;
                    }

                    String stationName = getString(item, "name");
                    if (stationName == null || stationName.trim().isEmpty()) {
                        log.warn("station_name이 비어있는 데이터 건너뜀: opinetId={}", opinetId);
                        continue;
                    }

                    ExternalGasStation station = ExternalGasStation.builder()
                            .opinetId(opinetId.trim())
                            .stationName(stationName.trim())
                            .brand(getString(item, "brand"))
                            .address(getString(item, "address"))
                            .latitude(parseBigDecimal(getString(item, "latitude")))
                            .longitude(parseBigDecimal(getString(item, "longitude")))
                            .phone(getString(item, "phone"))
                            .apiRawData(objectMapper.writeValueAsString(item))
                            .build();

                    stations.add(station);
                } catch (Exception e) {
                    log.error("주유소 데이터 변환 실패: {}", item, e);
                }
            }

            log.info("주유소 데이터 변환 완료: {} 건", stations.size());
            return stations;

        } catch (Exception e) {
            log.error("주유소 데이터 변환 중 오류 발생", e);
            throw new RuntimeException("주유소 데이터 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * API 응답 데이터를 GasPrice Entity로 변환합니다.
     */
    @SuppressWarnings("unchecked")
    public List<ExternalGasPrice> transformApiDataToPriceEntity(Map<String, Object> apiResponse) {
        List<ExternalGasPrice> prices = new ArrayList<>();
        LocalDateTime fetchedAt = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        try {
            List<Map<String, Object>> info = (List<Map<String, Object>>) apiResponse.get("info");

            if (info == null || info.isEmpty()) {
                log.warn("API 응답에 info가 없거나 비어있습니다.");
                return prices;
            }

            for (Map<String, Object> item : info) {
                try {
                    String opinetId = getString(item, "id");
                    if (opinetId == null || opinetId.trim().isEmpty()) {
                        continue;
                    }

                    String priceDateStr = getString(item, "price_date");
                    LocalDate priceDate = parsePriceDate(priceDateStr, today);

                    ExternalGasPrice price = ExternalGasPrice.builder()
                            .opinetId(opinetId.trim())
                            .gasolinePrice(parseInteger(getString(item, "gasoline")))
                            .premiumGasolinePrice(parseInteger(getString(item, "premium_gasoline")))
                            .dieselPrice(parseInteger(getString(item, "diesel")))
                            .lpgPrice(parseInteger(getString(item, "lpg")))
                            .priceDate(priceDate)
                            .apiRawData(objectMapper.writeValueAsString(item))
                            .fetchedAt(fetchedAt)
                            .build();

                    prices.add(price);
                } catch (Exception e) {
                    log.error("주유소 가격 데이터 변환 실패: {}", item, e);
                }
            }

            log.info("주유소 가격 데이터 변환 완료: {} 건", prices.size());
            return prices;

        } catch (Exception e) {
            log.error("주유소 가격 데이터 변환 중 오류 발생", e);
            throw new RuntimeException("주유소 가격 데이터 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 주유소 기본 정보를 DB에 저장합니다 (upsert).
     */
    @Transactional
    public int saveGasStations(List<ExternalGasStation> stations) {
        int count = 0;

        for (ExternalGasStation station : stations) {
            try {
                gasStationMapper.upsert(station);
                count++;
            } catch (Exception e) {
                log.error("주유소 저장 실패: {}", station.getOpinetId(), e);
            }
        }

        log.info("주유소 데이터 저장 완료: {} 건", count);
        return count;
    }

    /**
     * 주유소 가격 정보를 DB에 저장합니다 (upsert).
     * FK 제약조건을 고려하여 존재하는 주유소만 저장합니다.
     */
    @Transactional
    public int saveGasPrices(List<ExternalGasPrice> prices) {
        int count = 0;
        int skipped = 0;

        for (ExternalGasPrice price : prices) {
            try {
                // FK 검증: 주유소가 존재하는지 확인
                if (!gasStationMapper.existsByOpinetId(price.getOpinetId())) {
                    log.warn("존재하지 않는 주유소의 가격 데이터 건너뜀: {}", price.getOpinetId());
                    skipped++;
                    continue;
                }

                gasPriceMapper.upsert(price);
                count++;
            } catch (Exception e) {
                log.error("주유소 가격 저장 실패: {}", price.getOpinetId(), e);
            }
        }

        log.info("주유소 가격 데이터 저장 완료: {} 건 (건너뜀: {} 건)", count, skipped);
        return count;
    }

    /**
     * 외부 API 동기화 전체 프로세스를 실행합니다.
     */
    @Transactional
    public void syncGasPricesFromExternalApi() {
        syncGasPricesFromExternalApi(null, null);
    }

    /**
     * 외부 API 동기화 전체 프로세스를 실행합니다 (중단 체크 포함).
     */
    @Transactional
    public void syncGasPricesFromExternalApi(String sessionId, SyncManager syncManager) {
        try {
            log.info("=== 주유소 가격 데이터 동기화 시작 ===");

            // 중단 체크
            if (syncManager != null && sessionId != null) {
                syncManager.checkCancellation(sessionId);
            }

            // 1. API에서 데이터 가져오기
            Map<String, Object> apiResponse = fetchGasPriceDataFromApi();

            // 중단 체크
            if (syncManager != null && sessionId != null) {
                syncManager.checkCancellation(sessionId);
            }

            // 2-1. 주유소 기본 정보 변환 및 저장
            List<ExternalGasStation> stations = transformApiDataToStationEntity(apiResponse);
            int stationCount = saveGasStations(stations);

            // 중단 체크
            if (syncManager != null && sessionId != null) {
                syncManager.checkCancellation(sessionId);
            }

            // 2-2. 주유소 가격 정보 변환 및 저장
            List<ExternalGasPrice> prices = transformApiDataToPriceEntity(apiResponse);
            int priceCount = saveGasPrices(prices);

            log.info("=== 주유소 데이터 동기화 완료: 주유소 {} 건, 가격 {} 건 ===", stationCount, priceCount);

        } catch (Exception e) {
            log.error("주유소 가격 데이터 동기화 실패", e);
            throw new RuntimeException("주유소 가격 데이터 동기화 실패: " + e.getMessage(), e);
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
     * String을 Integer로 변환합니다.
     */
    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            log.warn("Integer 파싱 실패: {}", value);
            return 0;
        }
    }

    /**
     * String을 BigDecimal로 변환합니다.
     */
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            log.warn("BigDecimal 파싱 실패: {}", value);
            return null;
        }
    }

    /**
     * 가격 날짜를 파싱합니다. 없으면 오늘 날짜 사용.
     */
    private LocalDate parsePriceDate(String dateStr, LocalDate defaultDate) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return defaultDate;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}, 오늘 날짜 사용", dateStr);
            return defaultDate;
        }
    }
}
