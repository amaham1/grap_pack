package com.example.cms.user.content.mapper;

import com.example.cms.user.content.model.UserExhibitionRequest;
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
     * 노출 설정된 공연/전시 목록 조회 (탭별 필터링)
     * @param tab 탭 구분 (ongoing: 진행 중, upcoming: 다가올 공연, ended: 끝난 공연)
     */
    List<Map<String, Object>> selectVisibleExhibitionList(@Param("keyword") String keyword,
                                                            @Param("tab") String tab,
                                                            @Param("offset") int offset,
                                                            @Param("size") int size);

    /**
     * 노출 설정된 공연/전시 전체 개수 조회 (탭별 필터링)
     * @param tab 탭 구분 (ongoing: 진행 중, upcoming: 다가올 공연, ended: 끝난 공연)
     */
    int selectVisibleExhibitionCount(@Param("keyword") String keyword, @Param("tab") String tab);

    /**
     * 공연/전시 상세 조회 (노출 설정된 것만)
     */
    Map<String, Object> selectVisibleExhibitionById(@Param("id") Long id);

    /**
     * 사용자 공연/전시 등록 요청
     * @param request 등록 요청 데이터
     */
    void insertExhibitionRequest(UserExhibitionRequest request);

    /**
     * 마지막 INSERT ID 조회
     */
    Long selectLastInsertId();
}
