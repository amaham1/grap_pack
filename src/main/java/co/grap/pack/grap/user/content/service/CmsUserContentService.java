package co.grap.pack.grap.user.content.service;

import co.grap.pack.grap.admin.image.mapper.CmsAdminImageMapper;
import co.grap.pack.grap.admin.image.model.CmsAdminImage;
import co.grap.pack.grap.user.content.mapper.CmsUserContentMapper;
import co.grap.pack.grap.user.content.model.CmsUserContent;
import co.grap.pack.grap.user.content.model.CmsUserContentSearchParam;
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
public class CmsUserContentService {

    private final CmsUserContentMapper userContentMapper;
    private final CmsAdminImageMapper adminImageMapper;

    /**
     * 공개된 콘텐츠 목록 조회 (페이징)
     */
    public Map<String, Object> getContentList(CmsUserContentSearchParam searchParam) {
        // 기본값 설정
        if (searchParam.getPage() == null || searchParam.getPage() < 1) {
            searchParam.setPage(1);
        }
        if (searchParam.getSize() == null || searchParam.getSize() < 1) {
            searchParam.setSize(10);
        }

        List<CmsUserContent> contentList = userContentMapper.selectPublishedContentList(searchParam);
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
    public CmsUserContent getContent(Long contentId) {
        CmsUserContent content = userContentMapper.selectPublishedContentById(contentId);

        if (content != null) {
            // 조회수 증가
            userContentMapper.updateViewCount(contentId);

            // 이미지 목록 조회
            List<CmsAdminImage> images = adminImageMapper.selectImageListByContentId(contentId);
            List<String> imageList = images.stream()
                    .map(image -> "/uploads/" + image.getSavedName())
                    .collect(Collectors.toList());
            content.setImageList(imageList);
        }

        return content;
    }
}
