package com.example.cms.admin.content.mapper;

import com.example.cms.admin.content.model.Content;
import com.example.cms.admin.content.model.ContentSearchParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 관리자 콘텐츠 Mapper
 */
@Mapper
public interface AdminContentMapper {

    /**
     * 콘텐츠 목록 조회
     */
    List<Content> selectContentList(ContentSearchParam searchParam);

    /**
     * 콘텐츠 전체 개수 조회
     */
    int selectContentCount(ContentSearchParam searchParam);

    /**
     * 콘텐츠 상세 조회
     */
    Content selectContentById(Long contentId);

    /**
     * 콘텐츠 등록
     */
    void insertContent(Content content);

    /**
     * 콘텐츠 수정
     */
    void updateContent(Content content);

    /**
     * 콘텐츠 삭제
     */
    void deleteContent(Long contentId);

    /**
     * 콘텐츠 공개/비공개 설정
     */
    void updateContentPublishStatus(Long contentId, Boolean isPublished);
}
