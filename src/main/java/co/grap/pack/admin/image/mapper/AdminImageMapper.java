package co.grap.pack.admin.image.mapper;

import co.grap.pack.admin.image.model.AdminImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 관리자 이미지 Mapper
 */
@Mapper
public interface AdminImageMapper {

    /**
     * 콘텐츠별 이미지 목록 조회
     */
    List<AdminImage> selectImageListByContentId(Long contentId);

    /**
     * 이미지 상세 조회
     */
    AdminImage selectImageById(Long imageId);

    /**
     * 이미지 등록
     */
    void insertImage(AdminImage image);

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
