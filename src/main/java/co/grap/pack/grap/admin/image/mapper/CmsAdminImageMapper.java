package co.grap.pack.grap.admin.image.mapper;

import co.grap.pack.grap.admin.image.model.CmsAdminImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 관리자 이미지 Mapper
 */
@Mapper
public interface CmsAdminImageMapper {

    /**
     * 콘텐츠별 이미지 목록 조회
     */
    List<CmsAdminImage> selectImageListByContentId(Long contentId);

    /**
     * 이미지 상세 조회
     */
    CmsAdminImage selectImageById(Long imageId);

    /**
     * 이미지 등록
     */
    void insertImage(CmsAdminImage image);

    /**
     * 이미지 삭제
     */
    void deleteImage(Long imageId);

    /**
     * 콘텐츠의 이미지 전체 삭제
     */
    void deleteImagesByContentId(Long contentId);

    /**
     * 콘텐츠의 이미지 개수 조회
     */
    int selectImageCountByContentId(Long contentId);
}
