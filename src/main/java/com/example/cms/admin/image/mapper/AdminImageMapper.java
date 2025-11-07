package com.example.cms.admin.image.mapper;

import com.example.cms.admin.image.model.Image;
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
    List<Image> selectImageListByContentId(Long contentId);

    /**
     * 이미지 상세 조회
     */
    Image selectImageById(Long imageId);

    /**
     * 이미지 등록
     */
    void insertImage(Image image);

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
