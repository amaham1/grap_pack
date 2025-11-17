package com.example.cms.user.content.service;

import com.example.cms.user.content.mapper.UserWelfareMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자 복지서비스 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserWelfareService {

    private final UserWelfareMapper welfareMapper;

    /**
     * 노출 설정된 복지서비스 목록 조회 (페이징)
     */
    public Map<String, Object> getWelfareList(String keyword, Integer page, Integer size) {
        // 기본값 설정
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 12;
        }

        int offset = (page - 1) * size;

        List<Map<String, Object>> welfareList = welfareMapper.selectVisibleWelfareList(keyword, offset, size);
        int totalCount = welfareMapper.selectVisibleWelfareCount(keyword);

        // 페이징 정보 계산
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> result = new HashMap<>();
        result.put("welfareList", welfareList);
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);
        result.put("size", size);
        result.put("keyword", keyword);

        return result;
    }

    /**
     * 복지서비스 상세 조회
     */
    public Map<String, Object> getWelfareDetail(Long id) {
        return welfareMapper.selectVisibleWelfareById(id);
    }
}
