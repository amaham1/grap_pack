package co.grap.pack.qrmanage.superadmin.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * QR 관리 서비스 최고관리자 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageSuperAdmin {

    /** 최고관리자 ID */
    private Long id;

    /** 사용자명 (로그인 ID) */
    private String username;

    /** 비밀번호 (BCrypt 암호화) */
    private String password;

    /** 이름 */
    private String name;

    /** 이메일 */
    private String email;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 수정일시 */
    private LocalDateTime updatedAt;
}
