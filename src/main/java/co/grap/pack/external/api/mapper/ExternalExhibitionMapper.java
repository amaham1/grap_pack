package co.grap.pack.external.api.mapper;

import co.grap.pack.external.api.model.ExternalExhibition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 공연/전시 Mapper
 */
@Mapper
public interface ExternalExhibitionMapper {
    /**
     * 공연/전시 정보를 저장합니다.
     */
    void insert(ExternalExhibition exhibition);

    /**
     * 공연/전시 정보를 업데이트합니다.
     */
    void update(ExternalExhibition exhibition);

    /**
     * original_api_id로 공연/전시를 조회합니다.
     */
    ExternalExhibition findByOriginalApiId(@Param("originalApiId") String originalApiId);

    /**
     * 공연/전시 목록을 조회합니다.
     */
    List<ExternalExhibition> findAll();

    /**
     * 노출된 공연/전시 목록을 조회합니다.
     */
    List<ExternalExhibition> findAllVisible();

    /**
     * original_api_id로 존재 여부를 확인합니다.
     */
    boolean existsByOriginalApiId(@Param("originalApiId") String originalApiId);

    /**
     * 공연/전시를 upsert합니다.
     * (존재하면 update, 없으면 insert)
     */
    void upsert(ExternalExhibition exhibition);
}
