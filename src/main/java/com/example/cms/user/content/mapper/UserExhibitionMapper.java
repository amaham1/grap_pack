package com.example.cms.user.content.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 사용자 공연/전시 Mapper
 */
@Mapper
public interface UserExhibitionMapper {

    /**
     * 노출 설정된 공연/전시 목록 조회
     */
    List<Map<String, Object>> selectVisibleExhibitionList(@Param("keyword") String keyword,
                                                            @Param("offset") int offset,
                                                            @Param("size") int size);

    /**
     * 노출 설정된 공연/전시 전체 개수 조회
     */
    int selectVisibleExhibitionCount(@Param("keyword") String keyword);

    /**
     * 공연/전시 상세 조회 (노출 설정된 것만)
     */
    Map<String, Object> selectVisibleExhibitionById(@Param("id") Long id);
}
