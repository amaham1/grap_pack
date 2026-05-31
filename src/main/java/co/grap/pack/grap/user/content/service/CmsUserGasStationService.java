package co.grap.pack.grap.user.content.service;

import co.grap.pack.common.util.PaginationUtil;
import co.grap.pack.grap.user.content.mapper.CmsUserGasStationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자 주유소 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CmsUserGasStationService {

    private final CmsUserGasStationMapper gasStationMapper;
    private static final int DEFAULT_GAS_STATION_PAGE_SIZE = 20;  // 주유소는 기본 20개

    /**
     * 주유소 및 최신 가격 목록 조회 (페이징)
     */
    public Map<String, Object> getGasStationList(String keyword, Integer page, Integer size) {
        // 기본값 설정 (주유소는 기본 20개)
        Integer requestedSize = (size == null || size < 1) ? DEFAULT_GAS_STATION_PAGE_SIZE : size;

        // 전체 데이터 개수 조회
        int totalCount = gasStationMapper.selectGasStationCount(keyword);

        // 공통 페이징 정보 생성
        Map<String, Object> paginationResult = PaginationUtil.createPaginationResult(totalCount, page, requestedSize);
        int offset = (int) paginationResult.get("offset");
        int currentSize = (int) paginationResult.get("size");

        // 데이터 조회
        List<Map<String, Object>> gasStationList = gasStationMapper.selectGasStationListWithLatestPrice(keyword, offset, currentSize);

        // 최종 결과 구성
        Map<String, Object> result = new HashMap<>();
        result.putAll(paginationResult);  // 페이징 정보 추가 (offset, currentPage, size, totalPages, totalCount, pageInfo)
        result.put("gasStationList", gasStationList);
        result.put("keyword", keyword);

        return result;
    }

    /**
     * 주유소 및 최신 가격 상세 조회
     */
    public Map<String, Object> getGasStationDetail(Long id) {
        Map<String, Object> station = gasStationMapper.selectGasStationByIdWithLatestPrice(id);

        if (station != null && station.get("opinet_id") != null) {
            // 가격 이력 조회 (최근 30개)
            String opinetId = (String) station.get("opinet_id");
            List<Map<String, Object>> priceHistory = gasStationMapper.selectGasPriceHistory(opinetId, 30);
            station.put("priceHistory", priceHistory);
        }

        return station;
    }
}
