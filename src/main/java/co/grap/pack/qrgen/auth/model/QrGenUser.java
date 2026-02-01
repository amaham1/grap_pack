package co.grap.pack.qrgen.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * QR Generator 사용자 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrGenUser {

    /** 사용자 ID */
    private Long qrGenUserId;

    /** 로그인 ID */
    private String qrGenUserUsername;

    /** 비밀번호 (BCrypt) */
    private String qrGenUserPassword;

    /** 이메일 */
    private String qrGenUserEmail;

    /** 닉네임 */
    private String qrGenUserNickname;

    /** 활성화 여부 */
    private Boolean qrGenUserIsActive;

    /** 생성일시 */
    private LocalDateTime qrGenUserCreatedAt;

    /** 수정일시 */
    private LocalDateTime qrGenUserUpdatedAt;

    /** 마지막 로그인 일시 */
    private LocalDateTime qrGenUserLastLoginAt;
}
