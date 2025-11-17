package com.example.cms.external.api.mapper;

import com.example.cms.external.api.model.Exhibition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 공연/전시 Mapper
 */
@Mapper
public interface ExhibitionMapper {
    /**
     * 공연/전시 정보를 저장합니다.
     */
    void insert(Exhibition exhibition);

    /**
     * 공연/전시 정보를 업데이트합니다.
     */
    void update(Exhibition exhibition);

    /**
     * original_api_id로 공연/전시를 조회합니다.
     */
    Exhibition findByOriginalApiId(@Param("originalApiId") String originalApiId);

    /**
     * 공연/전시 목록을 조회합니다.
     */
    List<Exhibition> findAll();

    /**
     * 노출된 공연/전시 목록을 조회합니다.
     */
    List<Exhibition> findAllVisible();

    /**
     * original_api_id로 존재 여부를 확인합니다.
     */
    boolean existsByOriginalApiId(@Param("originalApiId") String originalApiId);

    /**
     * 공연/전시를 upsert합니다.
     * (존재하면 update, 없으면 insert)
     */
    void upsert(Exhibition exhibition);
}
