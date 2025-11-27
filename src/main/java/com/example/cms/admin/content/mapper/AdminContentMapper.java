package com.example.cms.admin.content.mapper;

import com.example.cms.admin.content.model.AdminContent;
import com.example.cms.admin.content.model.AdminContentSearchParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 관리자 콘텐츠 Mapper
 */
@Mapper
public interface AdminContentMapper {

    /**
     * 콘텐츠 목록 조회
     */
    List<AdminContent> selectContentList(AdminContentSearchParam searchParam);

    /**
     * 콘텐츠 전체 개수 조회
     */
    int selectContentCount(AdminContentSearchParam searchParam);

    /**
     * 콘텐츠 상세 조회
     */
    AdminContent selectContentById(Long contentId);

    /**
     * 콘텐츠 등록
     */
    void insertContent(AdminContent content);

    /**
     * 콘텐츠 수정
     */
    void updateContent(AdminContent content);

    /**
     * 콘텐츠 삭제
     */
    void deleteContent(Long contentId);

    /**
     * 콘텐츠 공개/비공개 설정
     */
    void updateContentPublishStatus(@Param("contentId") Long contentId, @Param("isPublished") Boolean isPublished);
}
