package com.example.cms.admin.external.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 관리자 주유소 Mapper
 */
@Mapper
public interface AdminGasStationMapper {

    /**
     * 주유소 목록 조회 (페이징)
     */
    List<Map<String, Object>> selectGasStationList(@Param("keyword") String keyword,
                                                     @Param("offset") int offset,
                                                     @Param("size") int size);

    /**
     * 주유소 전체 개수 조회
     */
    int selectGasStationCount(@Param("keyword") String keyword);

    /**
     * 주유소 상세 조회
     */
    Map<String, Object> selectGasStationById(@Param("id") Long id);

    /**
     * 주유소 삭제
     */
    void deleteGasStation(@Param("id") Long id);
}
