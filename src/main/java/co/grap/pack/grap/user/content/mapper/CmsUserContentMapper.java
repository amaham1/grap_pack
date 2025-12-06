package co.grap.pack.grap.user.content.mapper;

import co.grap.pack.grap.user.content.model.CmsUserContent;
import co.grap.pack.grap.user.content.model.CmsUserContentSearchParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 사용자 콘텐츠 Mapper
 */
@Mapper
public interface CmsUserContentMapper {

    /**
     * 공개된 콘텐츠 목록 조회
     */
    List<CmsUserContent> selectPublishedContentList(CmsUserContentSearchParam searchParam);

    /**
     * 공개된 콘텐츠 전체 개수 조회
     */
    int selectPublishedContentCount(CmsUserContentSearchParam searchParam);

    /**
     * 콘텐츠 상세 조회 (공개된 것만)
     */
    CmsUserContent selectPublishedContentById(Long contentId);

    /**
     * 조회수 증가
     */
    void updateViewCount(Long contentId);
}
