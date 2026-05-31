package co.grap.pack.admin.auth.mapper;

import co.grap.pack.admin.auth.model.AdminOperator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 통합 운영자 Mapper다.
 */
@Mapper
public interface AdminOperatorMapper {

    /**
     * 로그인 ID로 운영자를 조회한다.
     */
    AdminOperator findByLoginId(@Param("loginId") String loginId);

    /**
     * 마지막 로그인 시각을 갱신한다.
     */
    void updateLastLoginAt(@Param("id") Long id);
}
