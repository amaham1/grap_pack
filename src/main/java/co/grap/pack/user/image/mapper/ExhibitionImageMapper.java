package co.grap.pack.user.image.mapper;

import co.grap.pack.user.image.model.ExhibitionImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 공연/전시 이미지 Mapper
 */
@Mapper
public interface ExhibitionImageMapper {

    /**
     * 이미지 등록
     * @param image 이미지 정보
     */
    void insertImage(ExhibitionImage image);

    /**
     * 공연/전시별 이미지 목록 조회
     * @param exhibitionId 공연/전시 ID
     * @return 이미지 목록
     */
    List<ExhibitionImage> selectImagesByExhibitionId(@Param("exhibitionId") Long exhibitionId);

    /**
     * 이미지 단건 조회
     * @param imageId 이미지 ID
     * @return 이미지 정보
     */
    ExhibitionImage selectImageById(@Param("imageId") Long imageId);

    /**
     * 썸네일 이미지 조회
     * @param exhibitionId 공연/전시 ID
     * @return 썸네일 이미지 정보
     */
    ExhibitionImage selectThumbnailByExhibitionId(@Param("exhibitionId") Long exhibitionId);

    /**
     * 썸네일 설정 (기존 썸네일 해제 후 새로 설정)
     * @param exhibitionId 공연/전시 ID
     * @param imageId 썸네일로 설정할 이미지 ID
     */
    void updateThumbnail(@Param("exhibitionId") Long exhibitionId, @Param("imageId") Long imageId);

    /**
     * 썸네일 해제
     * @param exhibitionId 공연/전시 ID
     */
    void clearThumbnail(@Param("exhibitionId") Long exhibitionId);

    /**
     * 이미지 삭제
     * @param imageId 이미지 ID
     */
    void deleteImage(@Param("imageId") Long imageId);

    /**
     * 공연/전시별 이미지 전체 삭제
     * @param exhibitionId 공연/전시 ID
     */
    void deleteImagesByExhibitionId(@Param("exhibitionId") Long exhibitionId);

    /**
     * 이미지 개수 조회
     * @param exhibitionId 공연/전시 ID
     * @return 이미지 개수
     */
    int selectImageCountByExhibitionId(@Param("exhibitionId") Long exhibitionId);
}
