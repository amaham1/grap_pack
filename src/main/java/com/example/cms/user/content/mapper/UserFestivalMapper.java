package com.example.cms.user.content.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 사용자 축제/행사 Mapper
 */
@Mapper
public interface UserFestivalMapper {

    /**
     * 노출 설정된 축제/행사 목록 조회
     */
    List<Map<String, Object>> selectVisibleFestivalList(@Param("keyword") String keyword,
                                                          @Param("offset") int offset,
                                                          @Param("size") int size);

    /**
     * 노출 설정된 축제/행사 전체 개수 조회
     */
    int selectVisibleFestivalCount(@Param("keyword") String keyword);

    /**
     * 축제/행사 상세 조회 (노출 설정된 것만)
     */
    Map<String, Object> selectVisibleFestivalById(@Param("id") Long id);
}
