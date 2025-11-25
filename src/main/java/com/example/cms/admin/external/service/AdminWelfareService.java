package com.example.cms.admin.external.service;

import com.example.cms.admin.external.mapper.AdminWelfareMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 복지서비스 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminWelfareService {

    private final AdminWelfareMapper welfareMapper;

    /**
     * 복지서비스 목록 조회 (페이징)
     */
    public Map<String, Object> getWelfareList(String keyword, Integer page, Integer size) {
        // 기본값 설정
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        int offset = (page - 1) * size;

        List<Map<String, Object>> welfareList = welfareMapper.selectWelfareList(keyword, offset, size);
        int totalCount = welfareMapper.selectWelfareCount(keyword);

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
        return welfareMapper.selectWelfareById(id);
    }

    /**
     * 노출 여부 업데이트
     */
    @Transactional
    public void updateIsShow(Long id, Boolean isShow) {
        welfareMapper.updateIsShow(id, isShow);
    }

    /**
     * 관리자 메모 업데이트
     */
    @Transactional
    public void updateAdminMemo(Long id, String adminMemo) {
        welfareMapper.updateAdminMemo(id, adminMemo);
    }

    /**
     * 검수 처리
     */
    @Transactional
    public void updateConfirmStatus(Long id, String confirmStatus, String confirmedBy, String confirmMemo) {
        welfareMapper.updateConfirmStatus(id, confirmStatus, confirmedBy, confirmMemo);
    }

    /**
     * 복지서비스 삭제
     */
    @Transactional
    public void deleteWelfare(Long id) {
        welfareMapper.deleteWelfare(id);
    }

    /**
     * 노출 여부 일괄 업데이트
     */
    @Transactional
    public void bulkUpdateIsShow(List<Long> ids, Boolean isShow) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("선택된 항목이 없습니다.");
        }
        welfareMapper.bulkUpdateIsShow(ids, isShow);
    }
}
