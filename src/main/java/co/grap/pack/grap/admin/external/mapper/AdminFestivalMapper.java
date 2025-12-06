package co.grap.pack.grap.admin.external.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 관리자 축제/행사 Mapper
 */
@Mapper
public interface AdminFestivalMapper {

    /**
     * 축제/행사 목록 조회 (페이징)
     */
    List<Map<String, Object>> selectFestivalList(@Param("keyword") String keyword,
                                                   @Param("offset") int offset,
                                                   @Param("size") int size);

    /**
     * 축제/행사 전체 개수 조회
     */
    int selectFestivalCount(@Param("keyword") String keyword);

    /**
     * 축제/행사 상세 조회
     */
    Map<String, Object> selectFestivalById(@Param("id") Long id);

    /**
     * 노출 여부 업데이트
     */
    void updateIsShow(@Param("id") Long id, @Param("isShow") Boolean isShow);

    /**
     * 관리자 메모 업데이트
     */
    void updateAdminMemo(@Param("id") Long id, @Param("adminMemo") String adminMemo);

    /**
     * 검수 처리
     */
    void updateConfirmStatus(@Param("id") Long id,
                             @Param("confirmStatus") String confirmStatus,
                             @Param("confirmedBy") String confirmedBy,
                             @Param("confirmMemo") String confirmMemo);

    /**
     * 축제/행사 삭제
     */
    void deleteFestival(@Param("id") Long id);

    /**
     * 노출 여부 일괄 업데이트
     */
    void bulkUpdateIsShow(@Param("ids") List<Long> ids, @Param("isShow") Boolean isShow);
}
