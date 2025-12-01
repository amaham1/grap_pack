package com.example.cms.user.image.mapper;

import com.example.cms.user.image.model.WelfareServiceImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 복지서비스 이미지 Mapper
 */
@Mapper
public interface WelfareServiceImageMapper {

    /**
     * 이미지 등록
     * @param image 이미지 정보
     */
    void insertImage(WelfareServiceImage image);

    /**
     * 복지서비스별 이미지 목록 조회
     * @param welfareServiceId 복지서비스 ID
     * @return 이미지 목록
     */
    List<WelfareServiceImage> selectImagesByWelfareServiceId(@Param("welfareServiceId") Long welfareServiceId);

    /**
     * 이미지 단건 조회
     * @param imageId 이미지 ID
     * @return 이미지 정보
     */
    WelfareServiceImage selectImageById(@Param("imageId") Long imageId);

    /**
     * 썸네일 이미지 조회
     * @param welfareServiceId 복지서비스 ID
     * @return 썸네일 이미지 정보
     */
    WelfareServiceImage selectThumbnailByWelfareServiceId(@Param("welfareServiceId") Long welfareServiceId);

    /**
     * 썸네일 설정 (기존 썸네일 해제 후 새로 설정)
     * @param welfareServiceId 복지서비스 ID
     * @param imageId 썸네일로 설정할 이미지 ID
     */
    void updateThumbnail(@Param("welfareServiceId") Long welfareServiceId, @Param("imageId") Long imageId);

    /**
     * 썸네일 해제
     * @param welfareServiceId 복지서비스 ID
     */
    void clearThumbnail(@Param("welfareServiceId") Long welfareServiceId);

    /**
     * 이미지 삭제
     * @param imageId 이미지 ID
     */
    void deleteImage(@Param("imageId") Long imageId);

    /**
     * 복지서비스별 이미지 전체 삭제
     * @param welfareServiceId 복지서비스 ID
     */
    void deleteImagesByWelfareServiceId(@Param("welfareServiceId") Long welfareServiceId);

    /**
     * 이미지 개수 조회
     * @param welfareServiceId 복지서비스 ID
     * @return 이미지 개수
     */
    int selectImageCountByWelfareServiceId(@Param("welfareServiceId") Long welfareServiceId);
}
