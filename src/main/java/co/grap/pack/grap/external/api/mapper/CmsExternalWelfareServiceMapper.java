package co.grap.pack.grap.external.api.mapper;

import co.grap.pack.grap.external.api.model.CmsExternalWelfareService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 복지 서비스 Mapper
 */
@Mapper
public interface CmsExternalWelfareServiceMapper {
    /**
     * 복지 서비스 정보를 저장합니다.
     */
    void insert(CmsExternalWelfareService welfareService);

    /**
     * 복지 서비스 정보를 업데이트합니다.
     */
    void update(CmsExternalWelfareService welfareService);

    /**
     * original_api_id로 복지 서비스를 조회합니다.
     */
    CmsExternalWelfareService findByOriginalApiId(@Param("originalApiId") String originalApiId);

    /**
     * 복지 서비스 목록을 조회합니다.
     */
    List<CmsExternalWelfareService> findAll();

    /**
     * 노출된 복지 서비스 목록을 조회합니다.
     */
    List<CmsExternalWelfareService> findAllVisible();

    /**
     * original_api_id로 존재 여부를 확인합니다.
     */
    boolean existsByOriginalApiId(@Param("originalApiId") String originalApiId);

    /**
     * 복지 서비스를 upsert합니다.
     * (존재하면 update, 없으면 insert)
     */
    void upsert(CmsExternalWelfareService welfareService);
}
