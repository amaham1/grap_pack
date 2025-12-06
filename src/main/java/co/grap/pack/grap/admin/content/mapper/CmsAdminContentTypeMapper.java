package co.grap.pack.grap.admin.content.mapper;

import co.grap.pack.grap.admin.content.model.CmsAdminContentType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 관리자 콘텐츠 종류 Mapper
 */
@Mapper
public interface CmsAdminContentTypeMapper {

    /**
     * 콘텐츠 종류 목록 조회 (활성화된 것만)
     */
    List<CmsAdminContentType> selectActiveContentTypeList();

    /**
     * 콘텐츠 종류 전체 목록 조회
     */
    List<CmsAdminContentType> selectAllContentTypeList();

    /**
     * 콘텐츠 종류 상세 조회
     */
    CmsAdminContentType selectContentTypeById(Long contentTypeId);

    /**
     * 콘텐츠 종류 등록
     */
    void insertContentType(CmsAdminContentType contentType);

    /**
     * 콘텐츠 종류 수정
     */
    void updateContentType(CmsAdminContentType contentType);

    /**
     * 콘텐츠 종류 삭제
     */
    void deleteContentType(Long contentTypeId);
}
