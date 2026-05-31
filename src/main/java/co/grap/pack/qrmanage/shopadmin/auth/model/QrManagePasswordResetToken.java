package co.grap.pack.qrmanage.shopadmin.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 비밀번호 재설정 토큰 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManagePasswordResetToken {

    /** 토큰 ID */
    private Long id;

    /** 상점관리자 ID */
    private Long shopAdminId;

    /** 토큰 문자열 (UUID) */
    private String token;

    /** 만료일시 */
    private LocalDateTime expiresAt;

    /** 사용 여부 */
    private Boolean used;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /**
     * 토큰이 유효한지 확인
     */
    public boolean isValid() {
        return !used && LocalDateTime.now().isBefore(expiresAt);
    }
}
