package co.grap.pack.grap.auth.mapper;

import co.grap.pack.grap.auth.model.CmsAdmin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 인증 Mapper
 */
@Mapper
public interface CmsAuthMapper {

    /**
     * 사용자명으로 관리자 조회
     * @param username 사용자명
     * @return 관리자 정보
     */
    CmsAdmin selectAdminByUsername(String username);

    /**
     * 관리자 등록
     * @param admin 관리자 정보
     */
    void insertAdmin(CmsAdmin admin);
}
