package com.example.cms.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 관리자 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    /**
     * 관리자 ID
     */
    private Long adminId;

    /**
     * 사용자명 (로그인 ID)
     */
    private String username;

    /**
     * 비밀번호 (암호화)
     */
    private String password;

    /**
     * 이름
     */
    private String name;

    /**
     * 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;
}
