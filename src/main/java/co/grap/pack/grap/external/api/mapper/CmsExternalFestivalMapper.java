package co.grap.pack.grap.external.api.mapper;

import co.grap.pack.grap.external.api.model.CmsExternalFestival;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 축제/행사 Mapper
 */
@Mapper
public interface CmsExternalFestivalMapper {
    /**
     * 축제/행사 정보를 저장합니다.
     */
    void insert(CmsExternalFestival festival);

    /**
     * 축제/행사 정보를 업데이트합니다.
     */
    void update(CmsExternalFestival festival);

    /**
     * original_api_id로 축제/행사를 조회합니다.
     */
    CmsExternalFestival findByOriginalApiId(@Param("originalApiId") String originalApiId);

    /**
     * 축제/행사 목록을 조회합니다.
     */
    List<CmsExternalFestival> findAll();

    /**
     * 노출된 축제/행사 목록을 조회합니다.
     */
    List<CmsExternalFestival> findAllVisible();

    /**
     * original_api_id로 존재 여부를 확인합니다.
     */
    boolean existsByOriginalApiId(@Param("originalApiId") String originalApiId);

    /**
     * 축제/행사를 upsert합니다.
     * (존재하면 update, 없으면 insert)
     */
    void upsert(CmsExternalFestival festival);
}
