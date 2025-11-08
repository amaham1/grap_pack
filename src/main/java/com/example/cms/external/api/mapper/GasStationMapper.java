package com.example.cms.external.api.mapper;

import com.example.cms.external.api.model.GasStation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 주유소 Mapper
 */
@Mapper
public interface GasStationMapper {
    /**
     * 주유소 정보를 저장합니다.
     */
    void insert(GasStation gasStation);

    /**
     * 주유소 정보를 업데이트합니다.
     */
    void update(GasStation gasStation);

    /**
     * opinet_id로 주유소를 조회합니다.
     */
    GasStation findByOpinetId(@Param("opinetId") String opinetId);

    /**
     * 주유소 목록을 조회합니다.
     */
    List<GasStation> findAll();

    /**
     * opinet_id로 존재 여부를 확인합니다.
     */
    boolean existsByOpinetId(@Param("opinetId") String opinetId);

    /**
     * 주유소를 upsert합니다.
     * (존재하면 update, 없으면 insert)
     */
    void upsert(GasStation gasStation);
}
