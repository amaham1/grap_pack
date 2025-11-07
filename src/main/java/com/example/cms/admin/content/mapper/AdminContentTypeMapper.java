package com.example.cms.admin.content.mapper;

import com.example.cms.admin.content.model.ContentType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 관리자 콘텐츠 종류 Mapper
 */
@Mapper
public interface AdminContentTypeMapper {

    /**
     * 콘텐츠 종류 목록 조회 (활성화된 것만)
     */
    List<ContentType> selectActiveContentTypeList();

    /**
     * 콘텐츠 종류 전체 목록 조회
     */
    List<ContentType> selectAllContentTypeList();

    /**
     * 콘텐츠 종류 상세 조회
     */
    ContentType selectContentTypeById(Long contentTypeId);

    /**
     * 콘텐츠 종류 등록
     */
    void insertContentType(ContentType contentType);

    /**
     * 콘텐츠 종류 수정
     */
    void updateContentType(ContentType contentType);

    /**
     * 콘텐츠 종류 삭제
     */
    void deleteContentType(Long contentTypeId);
}
