package com.example.cms.admin.external.service;

import com.example.cms.admin.external.mapper.AdminFestivalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 축제/행사 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminFestivalService {

    private final AdminFestivalMapper festivalMapper;

    /**
     * 축제/행사 목록 조회 (페이징)
     */
    public Map<String, Object> getFestivalList(String keyword, Integer page, Integer size) {
        // 기본값 설정
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        int offset = (page - 1) * size;

        List<Map<String, Object>> festivalList = festivalMapper.selectFestivalList(keyword, offset, size);
        int totalCount = festivalMapper.selectFestivalCount(keyword);

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
        return festivalMapper.selectFestivalById(id);
    }

    /**
     * 노출 여부 업데이트
     */
    @Transactional
    public void updateIsShow(Long id, Boolean isShow) {
        festivalMapper.updateIsShow(id, isShow);
    }

    /**
     * 관리자 메모 업데이트
     */
    @Transactional
    public void updateAdminMemo(Long id, String adminMemo) {
        festivalMapper.updateAdminMemo(id, adminMemo);
    }

    /**
     * 검수 처리
     */
    @Transactional
    public void updateConfirmStatus(Long id, String confirmStatus, String confirmedBy, String confirmMemo) {
        festivalMapper.updateConfirmStatus(id, confirmStatus, confirmedBy, confirmMemo);
    }

    /**
     * 축제/행사 삭제
     */
    @Transactional
    public void deleteFestival(Long id) {
        festivalMapper.deleteFestival(id);
    }

    /**
     * 노출 여부 일괄 업데이트
     */
    @Transactional
    public void bulkUpdateIsShow(List<Long> ids, Boolean isShow) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("선택된 항목이 없습니다.");
        }
        festivalMapper.bulkUpdateIsShow(ids, isShow);
    }
}
