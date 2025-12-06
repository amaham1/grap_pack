package co.grap.pack.grap.external.api.mapper;

import co.grap.pack.grap.external.api.model.CmsExternalGasStation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 주유소 Mapper
 */
@Mapper
public interface CmsExternalGasStationMapper {
    /**
     * 주유소 정보를 저장합니다.
     */
    void insert(CmsExternalGasStation gasStation);

    /**
     * 주유소 정보를 업데이트합니다.
     */
    void update(CmsExternalGasStation gasStation);

    /**
     * opinet_id로 주유소를 조회합니다.
     */
    CmsExternalGasStation findByOpinetId(@Param("opinetId") String opinetId);

    /**
     * 주유소 목록을 조회합니다.
     */
    List<CmsExternalGasStation> findAll();

    /**
     * opinet_id로 존재 여부를 확인합니다.
     */
    boolean existsByOpinetId(@Param("opinetId") String opinetId);

    /**
     * 주유소를 upsert합니다.
     * (존재하면 update, 없으면 insert)
     */
    void upsert(CmsExternalGasStation gasStation);
}
