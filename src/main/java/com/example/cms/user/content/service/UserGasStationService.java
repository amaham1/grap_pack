package com.example.cms.user.content.service;

import com.example.cms.user.content.mapper.UserGasStationMapper;
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
public class UserGasStationService {

    private final UserGasStationMapper gasStationMapper;

    /**
     * 주유소 및 최신 가격 목록 조회 (페이징)
     */
    public Map<String, Object> getGasStationList(String keyword, Integer page, Integer size) {
        // 기본값 설정
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        int offset = (page - 1) * size;

        List<Map<String, Object>> gasStationList = gasStationMapper.selectGasStationListWithLatestPrice(keyword, offset, size);
        int totalCount = gasStationMapper.selectGasStationCount(keyword);

        // 페이징 정보 계산
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> result = new HashMap<>();
        result.put("gasStationList", gasStationList);
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);
        result.put("size", size);
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
