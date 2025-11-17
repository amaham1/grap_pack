package com.example.cms.user.content.service;

import com.example.cms.user.content.mapper.UserFestivalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자 축제/행사 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFestivalService {

    private final UserFestivalMapper festivalMapper;

    /**
     * 노출 설정된 축제/행사 목록 조회 (페이징)
     */
    public Map<String, Object> getFestivalList(String keyword, Integer page, Integer size) {
        // 기본값 설정
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 12;
        }

        int offset = (page - 1) * size;

        List<Map<String, Object>> festivalList = festivalMapper.selectVisibleFestivalList(keyword, offset, size);
        int totalCount = festivalMapper.selectVisibleFestivalCount(keyword);

        // 페이징 정보 계산
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> result = new HashMap<>();
        result.put("festivalList", festivalList);
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);
        result.put("size", size);
        result.put("keyword", keyword);

        return result;
    }

    /**
     * 축제/행사 상세 조회
     */
    public Map<String, Object> getFestivalDetail(Long id) {
        return festivalMapper.selectVisibleFestivalById(id);
    }
}
