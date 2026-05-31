package co.grap.pack.grap.external.api.mapper;

import co.grap.pack.grap.external.api.model.CmsExternalExhibition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 공연/전시 Mapper
 */
@Mapper
public interface CmsExternalExhibitionMapper {
    /**
     * 공연/전시 정보를 저장합니다.
     */
    void insert(CmsExternalExhibition exhibition);

    /**
     * 공연/전시 정보를 업데이트합니다.
     */
    void update(CmsExternalExhibition exhibition);

    /**
     * original_api_id로 공연/전시를 조회합니다.
     */
    CmsExternalExhibition findByOriginalApiId(@Param("originalApiId") String originalApiId);

    /**
     * 공연/전시 목록을 조회합니다.
     */
    List<CmsExternalExhibition> findAll();

    /**
     * 노출된 공연/전시 목록을 조회합니다.
     */
    List<CmsExternalExhibition> findAllVisible();

    /**
     * original_api_id로 존재 여부를 확인합니다.
     */
    boolean existsByOriginalApiId(@Param("originalApiId") String originalApiId);

    /**
     * 공연/전시를 upsert합니다.
     * (존재하면 update, 없으면 insert)
     */
    void upsert(CmsExternalExhibition exhibition);
}
