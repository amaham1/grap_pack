package com.example.cms.admin.content.service;

import com.example.cms.admin.content.mapper.AdminContentTypeMapper;
import com.example.cms.admin.content.model.ContentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 관리자 콘텐츠 종류 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminContentTypeService {

    private final AdminContentTypeMapper adminContentTypeMapper;

    /**
     * 활성화된 콘텐츠 종류 목록 조회
     */
    public List<ContentType> getActiveContentTypeList() {
        return adminContentTypeMapper.selectActiveContentTypeList();
    }

    /**
     * 전체 콘텐츠 종류 목록 조회
     */
    public List<ContentType> getAllContentTypeList() {
        return adminContentTypeMapper.selectAllContentTypeList();
    }

    /**
     * 콘텐츠 종류 상세 조회
     */
    public ContentType getContentType(Long contentTypeId) {
        return adminContentTypeMapper.selectContentTypeById(contentTypeId);
    }

    /**
     * 콘텐츠 종류 등록
     */
    @Transactional
    public void createContentType(ContentType contentType) {
        if (contentType.getIsActive() == null) {
            contentType.setIsActive(true);
        }
        adminContentTypeMapper.insertContentType(contentType);
    }

    /**
     * 콘텐츠 종류 수정
     */
    @Transactional
    public void updateContentType(ContentType contentType) {
        adminContentTypeMapper.updateContentType(contentType);
    }

    /**
     * 콘텐츠 종류 삭제
     */
    @Transactional
    public void deleteContentType(Long contentTypeId) {
        adminContentTypeMapper.deleteContentType(contentTypeId);
    }
}
