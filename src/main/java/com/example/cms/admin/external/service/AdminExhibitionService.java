package com.example.cms.admin.external.service;

import com.example.cms.admin.external.mapper.AdminExhibitionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 공연/전시 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminExhibitionService {

    private final AdminExhibitionMapper exhibitionMapper;

    /**
     * 공연/전시 목록 조회 (페이징)
     */
    public Map<String, Object> getExhibitionList(String keyword, Integer page, Integer size) {
        // 기본값 설정
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        int offset = (page - 1) * size;

        List<Map<String, Object>> exhibitionList = exhibitionMapper.selectExhibitionList(keyword, offset, size);
        int totalCount = exhibitionMapper.selectExhibitionCount(keyword);

        // 페이징 정보 계산
        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> result = new HashMap<>();
        result.put("exhibitionList", exhibitionList);
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);
        result.put("size", size);
        result.put("keyword", keyword);

        return result;
    }

    /**
     * 공연/전시 상세 조회
     */
    public Map<String, Object> getExhibitionDetail(Long id) {
        return exhibitionMapper.selectExhibitionById(id);
    }

    /**
     * 노출 여부 업데이트
     */
    @Transactional
    public void updateIsShow(Long id, Boolean isShow) {
        exhibitionMapper.updateIsShow(id, isShow);
    }

    /**
     * 관리자 메모 업데이트
     */
    @Transactional
    public void updateAdminMemo(Long id, String adminMemo) {
        exhibitionMapper.updateAdminMemo(id, adminMemo);
    }

    /**
     * 검수 처리
     */
    @Transactional
    public void updateConfirmStatus(Long id, String confirmStatus, String confirmedBy, String confirmMemo) {
        exhibitionMapper.updateConfirmStatus(id, confirmStatus, confirmedBy, confirmMemo);
    }

    /**
     * 공연/전시 삭제
     */
    @Transactional
    public void deleteExhibition(Long id) {
        exhibitionMapper.deleteExhibition(id);
    }

    /**
     * 노출 여부 일괄 업데이트
     */
    @Transactional
    public void bulkUpdateIsShow(List<Long> ids, Boolean isShow) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("선택된 항목이 없습니다.");
        }
        exhibitionMapper.bulkUpdateIsShow(ids, isShow);
    }
}
