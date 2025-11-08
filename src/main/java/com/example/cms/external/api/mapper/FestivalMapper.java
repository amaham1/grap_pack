package com.example.cms.external.api.mapper;

import com.example.cms.external.api.model.Festival;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 축제/행사 Mapper
 */
@Mapper
public interface FestivalMapper {
    /**
     * 축제/행사 정보를 저장합니다.
     */
    void insert(Festival festival);

    /**
     * 축제/행사 정보를 업데이트합니다.
     */
    void update(Festival festival);

    /**
     * original_api_id로 축제/행사를 조회합니다.
     */
    Festival findByOriginalApiId(@Param("originalApiId") String originalApiId);

    /**
     * 축제/행사 목록을 조회합니다.
     */
    List<Festival> findAll();

    /**
     * 노출된 축제/행사 목록을 조회합니다.
     */
    List<Festival> findAllVisible();

    /**
     * original_api_id로 존재 여부를 확인합니다.
     */
    boolean existsByOriginalApiId(@Param("originalApiId") String originalApiId);

    /**
     * 축제/행사를 upsert합니다.
     * (존재하면 update, 없으면 insert)
     */
    void upsert(Festival festival);
}
