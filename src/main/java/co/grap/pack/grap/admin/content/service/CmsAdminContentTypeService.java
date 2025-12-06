package co.grap.pack.grap.admin.content.service;

import co.grap.pack.grap.admin.content.mapper.CmsAdminContentTypeMapper;
import co.grap.pack.grap.admin.content.model.CmsAdminContentType;
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
public class CmsAdminContentTypeService {

    private final CmsAdminContentTypeMapper adminContentTypeMapper;

    /**
     * 활성화된 콘텐츠 종류 목록 조회
     */
    public List<CmsAdminContentType> getActiveContentTypeList() {
        return adminContentTypeMapper.selectActiveContentTypeList();
    }

    /**
     * 전체 콘텐츠 종류 목록 조회
     */
    public List<CmsAdminContentType> getAllContentTypeList() {
        return adminContentTypeMapper.selectAllContentTypeList();
    }

    /**
     * 콘텐츠 종류 상세 조회
     */
    public CmsAdminContentType getContentType(Long contentTypeId) {
        return adminContentTypeMapper.selectContentTypeById(contentTypeId);
    }

    /**
     * 콘텐츠 종류 등록
     */
    @Transactional
    public void createContentType(CmsAdminContentType contentType) {
        if (contentType.getIsActive() == null) {
            contentType.setIsActive(true);
        }
        adminContentTypeMapper.insertContentType(contentType);
    }

    /**
     * 콘텐츠 종류 수정
     */
    @Transactional
    public void updateContentType(CmsAdminContentType contentType) {
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
