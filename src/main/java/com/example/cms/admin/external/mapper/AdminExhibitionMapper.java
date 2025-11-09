package com.example.cms.admin.external.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 관리자 공연/전시 Mapper
 */
@Mapper
public interface AdminExhibitionMapper {

    /**
     * 공연/전시 목록 조회 (페이징)
     */
    List<Map<String, Object>> selectExhibitionList(@Param("keyword") String keyword,
                                                     @Param("offset") int offset,
                                                     @Param("size") int size);

    /**
     * 공연/전시 전체 개수 조회
     */
    int selectExhibitionCount(@Param("keyword") String keyword);

    /**
     * 공연/전시 상세 조회
     */
    Map<String, Object> selectExhibitionById(@Param("id") Long id);

    /**
     * 노출 여부 업데이트
     */
    void updateIsShow(@Param("id") Long id, @Param("isShow") Boolean isShow);

    /**
     * 관리자 메모 업데이트
     */
    void updateAdminMemo(@Param("id") Long id, @Param("adminMemo") String adminMemo);

    /**
     * 공연/전시 삭제
     */
    void deleteExhibition(@Param("id") Long id);
}
