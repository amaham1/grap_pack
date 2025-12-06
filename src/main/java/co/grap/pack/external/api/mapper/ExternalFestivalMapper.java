package co.grap.pack.external.api.mapper;

import co.grap.pack.external.api.model.ExternalFestival;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 축제/행사 Mapper
 */
@Mapper
public interface ExternalFestivalMapper {
    /**
     * 축제/행사 정보를 저장합니다.
     */
    void insert(ExternalFestival festival);

    /**
     * 축제/행사 정보를 업데이트합니다.
     */
    void update(ExternalFestival festival);

    /**
     * original_api_id로 축제/행사를 조회합니다.
     */
    ExternalFestival findByOriginalApiId(@Param("originalApiId") String originalApiId);

    /**
     * 축제/행사 목록을 조회합니다.
     */
    List<ExternalFestival> findAll();

    /**
     * 노출된 축제/행사 목록을 조회합니다.
     */
    List<ExternalFestival> findAllVisible();

    /**
     * original_api_id로 존재 여부를 확인합니다.
     */
    boolean existsByOriginalApiId(@Param("originalApiId") String originalApiId);

    /**
     * 축제/행사를 upsert합니다.
     * (존재하면 update, 없으면 insert)
     */
    void upsert(ExternalFestival festival);
}
