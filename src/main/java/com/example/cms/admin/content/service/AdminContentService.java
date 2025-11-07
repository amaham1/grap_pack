package com.example.cms.admin.content.service;

import com.example.cms.admin.content.mapper.AdminContentMapper;
import com.example.cms.admin.content.model.Content;
import com.example.cms.admin.content.model.ContentSearchParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자 콘텐츠 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminContentService {

    private final AdminContentMapper adminContentMapper;

    /**
     * 콘텐츠 목록 조회 (페이징)
     */
    public Map<String, Object> getContentList(ContentSearchParam searchParam) {
        // 기본값 설정
        if (searchParam.getPage() == null || searchParam.getPage() < 1) {
            searchParam.setPage(1);
        }
        if (searchParam.getSize() == null || searchParam.getSize() < 1) {
            searchParam.setSize(10);
        }

        List<Content> contentList = adminContentMapper.selectContentList(searchParam);
        int totalCount = adminContentMapper.selectContentCount(searchParam);

        // 페이징 정보 계산
        int totalPages = (int) Math.ceil((double) totalCount / searchParam.getSize());

        Map<String, Object> result = new HashMap<>();
        result.put("contentList", contentList);
        result.put("currentPage", searchParam.getPage());
        result.put("totalPages", totalPages);
        result.put("totalCount", totalCount);
        result.put("size", searchParam.getSize());

        return result;
    }

    /**
     * 콘텐츠 상세 조회
     */
    public Content getContent(Long contentId) {
        return adminContentMapper.selectContentById(contentId);
    }

    /**
     * 콘텐츠 등록
     */
    @Transactional
    public void createContent(Content content) {
        if (content.getIsPublished() == null) {
            content.setIsPublished(false);
        }
        adminContentMapper.insertContent(content);
    }

    /**
     * 콘텐츠 수정
     */
    @Transactional
    public void updateContent(Content content) {
        adminContentMapper.updateContent(content);
    }

    /**
     * 콘텐츠 삭제
     */
    @Transactional
    public void deleteContent(Long contentId) {
        adminContentMapper.deleteContent(contentId);
    }

    /**
     * 콘텐츠 공개/비공개 설정
     */
    @Transactional
    public void updatePublishStatus(Long contentId, Boolean isPublished) {
        adminContentMapper.updateContentPublishStatus(contentId, isPublished);
    }
}
