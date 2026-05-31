package co.grap.pack.grap.admin.content.mapper;

import co.grap.pack.grap.admin.content.model.CmsAdminContent;
import co.grap.pack.grap.admin.content.model.CmsAdminContentSearchParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 관리자 콘텐츠 Mapper
 */
@Mapper
public interface CmsAdminContentMapper {

    /**
     * 콘텐츠 목록 조회
     */
    List<CmsAdminContent> selectContentList(CmsAdminContentSearchParam searchParam);

    /**
     * 콘텐츠 전체 개수 조회
     */
    int selectContentCount(CmsAdminContentSearchParam searchParam);

    /**
     * 콘텐츠 상세 조회
     */
    CmsAdminContent selectContentById(Long contentId);

    /**
     * 콘텐츠 등록
     */
    void insertContent(CmsAdminContent content);

    /**
     * 콘텐츠 수정
     */
    void updateContent(CmsAdminContent content);

    /**
     * 콘텐츠 삭제
     */
    void deleteContent(Long contentId);

    /**
     * 콘텐츠 공개/비공개 설정
     */
    void updateContentPublishStatus(@Param("contentId") Long contentId, @Param("isPublished") Boolean isPublished);
}
