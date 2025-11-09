package com.example.cms.admin.external.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 관리자 복지서비스 Mapper
 */
@Mapper
public interface AdminWelfareMapper {

    /**
     * 복지서비스 목록 조회 (페이징)
     */
    List<Map<String, Object>> selectWelfareList(@Param("keyword") String keyword,
                                                  @Param("offset") int offset,
                                                  @Param("size") int size);

    /**
     * 복지서비스 전체 개수 조회
     */
    int selectWelfareCount(@Param("keyword") String keyword);

    /**
     * 복지서비스 상세 조회
     */
    Map<String, Object> selectWelfareById(@Param("id") Long id);

    /**
     * 노출 여부 업데이트
     */
    void updateIsShow(@Param("id") Long id, @Param("isShow") Boolean isShow);

    /**
     * 관리자 메모 업데이트
     */
    void updateAdminMemo(@Param("id") Long id, @Param("adminMemo") String adminMemo);

    /**
     * 복지서비스 삭제
     */
    void deleteWelfare(@Param("id") Long id);
}
