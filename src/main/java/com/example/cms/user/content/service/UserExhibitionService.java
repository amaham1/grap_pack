package com.example.cms.user.content.service;

import com.example.cms.common.util.PaginationUtil;
import com.example.cms.user.content.mapper.UserExhibitionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자 공연/전시 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserExhibitionService {

    private final UserExhibitionMapper exhibitionMapper;

    /**
     * 노출 설정된 공연/전시 목록 조회 (페이징)
     */
    public Map<String, Object> getExhibitionList(String keyword, Integer page, Integer size) {
        // 전체 데이터 개수 조회
        int totalCount = exhibitionMapper.selectVisibleExhibitionCount(keyword);

        // 공통 페이징 정보 생성
        Map<String, Object> paginationResult = PaginationUtil.createPaginationResult(totalCount, page, size);
        int offset = (int) paginationResult.get("offset");
        int currentSize = (int) paginationResult.get("size");

        // 데이터 조회
        List<Map<String, Object>> exhibitionList = exhibitionMapper.selectVisibleExhibitionList(keyword, offset, currentSize);

        // 각 공연/전시 데이터에 마감임박 계산 추가
        LocalDateTime now = LocalDateTime.now();
        for (Map<String, Object> exhibition : exhibitionList) {
            enrichExhibitionData(exhibition, now);
        }

        // 최종 결과 구성
        Map<String, Object> result = new HashMap<>();
        result.putAll(paginationResult);  // 페이징 정보 추가 (offset, currentPage, size, totalPages, totalCount, pageInfo)
        result.put("exhibitionList", exhibitionList);
        result.put("keyword", keyword);

        return result;
    }

    /**
     * 공연/전시 데이터 보강 (마감임박 계산)
     */
    private void enrichExhibitionData(Map<String, Object> exhibition, LocalDateTime now) {
        // 마감임박 계산 (종료일까지 남은 일수)
        Object endDateObj = exhibition.get("endDate");
        if (endDateObj instanceof LocalDateTime) {
            LocalDateTime endDate = (LocalDateTime) endDateObj;
            long daysUntilEnd = ChronoUnit.DAYS.between(now, endDate);
            exhibition.put("daysUntilEnd", daysUntilEnd);
        }
    }

    /**
     * 공연/전시 상세 조회
     */
    public Map<String, Object> getExhibitionDetail(Long id) {
        return exhibitionMapper.selectVisibleExhibitionById(id);
    }
}
