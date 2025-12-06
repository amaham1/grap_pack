package co.grap.pack.admin.content.service;

import co.grap.pack.admin.content.mapper.AdminContentTypeMapper;
import co.grap.pack.admin.content.model.AdminContentType;
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
    public List<AdminContentType> getActiveContentTypeList() {
        return adminContentTypeMapper.selectActiveContentTypeList();
    }

    /**
     * 전체 콘텐츠 종류 목록 조회
     */
    public List<AdminContentType> getAllContentTypeList() {
        return adminContentTypeMapper.selectAllContentTypeList();
    }

    /**
     * 콘텐츠 종류 상세 조회
     */
    public AdminContentType getContentType(Long contentTypeId) {
        return adminContentTypeMapper.selectContentTypeById(contentTypeId);
    }

    /**
     * 콘텐츠 종류 등록
     */
    @Transactional
    public void createContentType(AdminContentType contentType) {
        if (contentType.getIsActive() == null) {
            contentType.setIsActive(true);
        }
        adminContentTypeMapper.insertContentType(contentType);
    }

    /**
     * 콘텐츠 종류 수정
     */
    @Transactional
    public void updateContentType(AdminContentType contentType) {
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
