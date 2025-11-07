package com.example.cms.user.content.service;

import com.example.cms.admin.image.mapper.AdminImageMapper;
import com.example.cms.admin.image.model.Image;
import com.example.cms.user.content.mapper.UserContentMapper;
import com.example.cms.user.content.model.Content;
import com.example.cms.user.content.model.ContentSearchParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 사용자 콘텐츠 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserContentService {

    private final UserContentMapper userContentMapper;
    private final AdminImageMapper adminImageMapper;

    /**
     * 공개된 콘텐츠 목록 조회 (페이징)
     */
    public Map<String, Object> getContentList(ContentSearchParam searchParam) {
        // 기본값 설정
        if (searchParam.getPage() == null || searchParam.getPage() < 1) {
            searchParam.setPage(1);
        }
        if (searchParam.getSize() == null || searchParam.getSize() < 1) {
            searchParam.setSize(10);
        }

        List<Content> contentList = userContentMapper.selectPublishedContentList(searchParam);
        int totalCount = userContentMapper.selectPublishedContentCount(searchParam);

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
     * 콘텐츠 상세 조회 (조회수 증가)
     */
    @Transactional
    public Content getContent(Long contentId) {
        Content content = userContentMapper.selectPublishedContentById(contentId);

        if (content != null) {
            // 조회수 증가
            userContentMapper.updateViewCount(contentId);

            // 이미지 목록 조회
            List<Image> images = adminImageMapper.selectImageListByContentId(contentId);
            List<String> imageList = images.stream()
                    .map(image -> "/uploads/" + image.getSavedName())
                    .collect(Collectors.toList());
            content.setImageList(imageList);
        }

        return content;
    }
}
