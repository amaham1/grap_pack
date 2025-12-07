package co.grap.pack.qrmanage.shopadmin.auth.service;

import co.grap.pack.qrmanage.shopadmin.auth.mapper.QrManagePasswordResetTokenMapper;
import co.grap.pack.qrmanage.shopadmin.auth.mapper.QrManageShopAdminMapper;
import co.grap.pack.qrmanage.shopadmin.auth.model.QrManagePasswordResetToken;
import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdmin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 비밀번호 재설정 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrManagePasswordResetService {

    private final QrManagePasswordResetTokenMapper tokenMapper;
    private final QrManageShopAdminMapper shopAdminMapper;
    private final PasswordEncoder passwordEncoder;

    /** 토큰 유효 시간 (시간 단위) */
    private static final int TOKEN_VALIDITY_HOURS = 24;

    /**
     * 비밀번호 재설정 토큰 생성
     * @param email 이메일
     * @return 생성된 토큰 (이메일 발송용)
     */
    @Transactional
    public String createResetToken(String email) {
        log.info("✅ [CHECK] 비밀번호 재설정 토큰 생성 요청: {}", email);

        // 이메일로 상점관리자 조회
        QrManageShopAdmin shopAdmin = shopAdminMapper.findByEmail(email);
        if (shopAdmin == null) {
            log.warn("❌ [ERROR] 해당 이메일로 등록된 계정 없음: {}", email);
            // 보안상 이메일 존재 여부를 노출하지 않음
            return null;
        }

        // 기존 토큰 무효화
        tokenMapper.invalidateByShopAdminId(shopAdmin.getId());

        // 새 토큰 생성
        String tokenString = UUID.randomUUID().toString();
        QrManagePasswordResetToken token = QrManagePasswordResetToken.builder()
                .shopAdminId(shopAdmin.getId())
                .token(tokenString)
                .expiresAt(LocalDateTime.now().plusHours(TOKEN_VALIDITY_HOURS))
                .used(false)
                .build();

        tokenMapper.insert(token);
        log.info("✅ [CHECK] 비밀번호 재설정 토큰 생성 완료: shopAdminId={}", shopAdmin.getId());

        return tokenString;
    }

    /**
     * 토큰 유효성 검증
     * @param token 토큰 문자열
     * @return 유효하면 true
     */
    public boolean validateToken(String token) {
        QrManagePasswordResetToken resetToken = tokenMapper.findByToken(token);
        if (resetToken == null) {
            log.warn("❌ [ERROR] 존재하지 않는 토큰: {}", token);
            return false;
        }
        return resetToken.isValid();
    }

    /**
     * 비밀번호 재설정
     * @param token 토큰 문자열
     * @param newPassword 새 비밀번호
     * @return 성공 여부
     */
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        log.info("✅ [CHECK] 비밀번호 재설정 시도");

        QrManagePasswordResetToken resetToken = tokenMapper.findByToken(token);
        if (resetToken == null || !resetToken.isValid()) {
            log.warn("❌ [ERROR] 유효하지 않은 토큰");
            return false;
        }

        // 비밀번호 변경
        QrManageShopAdmin shopAdmin = shopAdminMapper.findById(resetToken.getShopAdminId());
        if (shopAdmin == null) {
            log.error("❌ [ERROR] 상점관리자를 찾을 수 없음: id={}", resetToken.getShopAdminId());
            return false;
        }

        shopAdmin.setPassword(passwordEncoder.encode(newPassword));
        shopAdminMapper.update(shopAdmin);

        // 토큰 사용 처리
        tokenMapper.markAsUsed(token);

        log.info("✅ [CHECK] 비밀번호 재설정 완료: shopAdminId={}", shopAdmin.getId());
        return true;
    }

    /**
     * 토큰으로 상점관리자 조회
     * @param token 토큰 문자열
     * @return 상점관리자 정보
     */
    public QrManageShopAdmin findShopAdminByToken(String token) {
        QrManagePasswordResetToken resetToken = tokenMapper.findByToken(token);
        if (resetToken == null || !resetToken.isValid()) {
            return null;
        }
        return shopAdminMapper.findById(resetToken.getShopAdminId());
    }
}
