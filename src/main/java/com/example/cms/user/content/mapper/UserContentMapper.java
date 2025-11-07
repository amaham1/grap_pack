package com.example.cms.user.content.mapper;

import com.example.cms.user.content.model.Content;
import com.example.cms.user.content.model.ContentSearchParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 사용자 콘텐츠 Mapper
 */
@Mapper
public interface UserContentMapper {

    /**
     * 공개된 콘텐츠 목록 조회
     */
    List<Content> selectPublishedContentList(ContentSearchParam searchParam);

    /**
     * 공개된 콘텐츠 전체 개수 조회
     */
    int selectPublishedContentCount(ContentSearchParam searchParam);

    /**
     * 콘텐츠 상세 조회 (공개된 것만)
     */
    Content selectPublishedContentById(Long contentId);

    /**
     * 조회수 증가
     */
    void updateViewCount(Long contentId);
}
