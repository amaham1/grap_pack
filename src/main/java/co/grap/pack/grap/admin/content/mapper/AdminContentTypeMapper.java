package co.grap.pack.grap.admin.content.mapper;

import co.grap.pack.grap.admin.content.model.AdminContentType;
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
    List<AdminContentType> selectActiveContentTypeList();

    /**
     * 콘텐츠 종류 전체 목록 조회
     */
    List<AdminContentType> selectAllContentTypeList();

    /**
     * 콘텐츠 종류 상세 조회
     */
    AdminContentType selectContentTypeById(Long contentTypeId);

    /**
     * 콘텐츠 종류 등록
     */
    void insertContentType(AdminContentType contentType);

    /**
     * 콘텐츠 종류 수정
     */
    void updateContentType(AdminContentType contentType);

    /**
     * 콘텐츠 종류 삭제
     */
    void deleteContentType(Long contentTypeId);
}
