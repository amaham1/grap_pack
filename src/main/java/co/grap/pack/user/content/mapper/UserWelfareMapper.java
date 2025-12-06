package co.grap.pack.user.content.mapper;

import co.grap.pack.user.content.model.UserWelfareRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 사용자 복지서비스 Mapper
 */
@Mapper
public interface UserWelfareMapper {

    /**
     * 노출 설정된 복지서비스 목록 조회
     */
    List<Map<String, Object>> selectVisibleWelfareList(@Param("keyword") String keyword,
                                                         @Param("offset") int offset,
                                                         @Param("size") int size);

    /**
     * 노출 설정된 복지서비스 전체 개수 조회
     */
    int selectVisibleWelfareCount(@Param("keyword") String keyword);

    /**
     * 복지서비스 상세 조회 (노출 설정된 것만)
     */
    Map<String, Object> selectVisibleWelfareById(@Param("id") Long id);

    /**
     * 사용자 복지서비스 등록 요청
     * @param request 등록 요청 데이터
     */
    void insertWelfareRequest(UserWelfareRequest request);

    /**
     * 마지막 INSERT ID 조회
     */
    Long selectLastInsertId();
}
