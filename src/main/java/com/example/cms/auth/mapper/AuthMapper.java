package com.example.cms.auth.mapper;

import com.example.cms.auth.model.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 인증 Mapper
 */
@Mapper
public interface AuthMapper {

    /**
     * 사용자명으로 관리자 조회
     * @param username 사용자명
     * @return 관리자 정보
     */
    Admin selectAdminByUsername(String username);

    /**
     * 관리자 등록
     * @param admin 관리자 정보
     */
    void insertAdmin(Admin admin);
}
