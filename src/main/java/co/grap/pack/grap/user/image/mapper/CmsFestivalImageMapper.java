package co.grap.pack.grap.user.image.mapper;

import co.grap.pack.grap.user.image.model.CmsFestivalImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 축제/행사 이미지 Mapper
 */
@Mapper
public interface CmsFestivalImageMapper {

    /**
     * 이미지 등록
     * @param image 이미지 정보
     */
    void insertImage(CmsFestivalImage image);

    /**
     * 축제/행사별 이미지 목록 조회
     * @param festivalId 축제/행사 ID
     * @return 이미지 목록
     */
    List<CmsFestivalImage> selectImagesByFestivalId(@Param("festivalId") Long festivalId);

    /**
     * 이미지 단건 조회
     * @param imageId 이미지 ID
     * @return 이미지 정보
     */
    CmsFestivalImage selectImageById(@Param("imageId") Long imageId);

    /**
     * 썸네일 이미지 조회
     * @param festivalId 축제/행사 ID
     * @return 썸네일 이미지 정보
     */
    CmsFestivalImage selectThumbnailByFestivalId(@Param("festivalId") Long festivalId);

    /**
     * 썸네일 설정 (기존 썸네일 해제 후 새로 설정)
     * @param festivalId 축제/행사 ID
     * @param imageId 썸네일로 설정할 이미지 ID
     */
    void updateThumbnail(@Param("festivalId") Long festivalId, @Param("imageId") Long imageId);

    /**
     * 썸네일 해제
     * @param festivalId 축제/행사 ID
     */
    void clearThumbnail(@Param("festivalId") Long festivalId);

    /**
     * 이미지 삭제
     * @param imageId 이미지 ID
     */
    void deleteImage(@Param("imageId") Long imageId);

    /**
     * 축제/행사별 이미지 전체 삭제
     * @param festivalId 축제/행사 ID
     */
    void deleteImagesByFestivalId(@Param("festivalId") Long festivalId);

    /**
     * 이미지 개수 조회
     * @param festivalId 축제/행사 ID
     * @return 이미지 개수
     */
    int selectImageCountByFestivalId(@Param("festivalId") Long festivalId);
}
