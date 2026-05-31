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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

/**
 * 비밀번호 재설정 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrManagePasswordResetService {

    private static final int TOKEN_VALIDITY_HOURS = 24;

    private final QrManagePasswordResetTokenMapper tokenMapper;
    private final QrManageShopAdminMapper shopAdminMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 비밀번호 재설정 토큰 생성
     *
     * @param email 이메일
     * @return 이메일로 발송할 원문 토큰
     */
    @Transactional
    public String createResetToken(String email) {
        log.info("✅ [CHECK] 비밀번호 재설정 토큰 생성 요청: email={}", maskEmail(email));

        QrManageShopAdmin shopAdmin = shopAdminMapper.findByEmail(email);
        if (shopAdmin == null) {
            log.warn("비밀번호 재설정 요청 계정 없음: email={}", maskEmail(email));
            return null;
        }

        tokenMapper.invalidateByShopAdminId(shopAdmin.getId());

        String rawToken = UUID.randomUUID().toString();
        QrManagePasswordResetToken token = QrManagePasswordResetToken.builder()
                .shopAdminId(shopAdmin.getId())
                .token(hashResetToken(rawToken))
                .expiresAt(LocalDateTime.now().plusHours(TOKEN_VALIDITY_HOURS))
                .used(false)
                .build();

        tokenMapper.insert(token);
        log.info("✅ [CHECK] 비밀번호 재설정 토큰 생성 완료: shopAdminId={}", shopAdmin.getId());

        return rawToken;
    }

    /**
     * 토큰 유효성 검증
     *
     * @param token 원문 토큰
     * @return 유효하면 true
     */
    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            log.warn("비밀번호 재설정 토큰 검증 실패: 빈 토큰");
            return false;
        }

        QrManagePasswordResetToken resetToken = tokenMapper.findByToken(hashResetToken(token));
        if (resetToken == null) {
            log.warn("비밀번호 재설정 토큰 검증 실패: 토큰 없음");
            return false;
        }
        return resetToken.isValid();
    }

    /**
     * 비밀번호 재설정
     *
     * @param token 원문 토큰
     * @param newPassword 새 비밀번호
     * @return 성공 여부
     */
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        log.info("✅ [CHECK] 비밀번호 재설정 시도");

        if (token == null || token.isBlank()) {
            log.warn("비밀번호 재설정 실패: 빈 토큰");
            return false;
        }

        String tokenHash = hashResetToken(token);
        QrManagePasswordResetToken resetToken = tokenMapper.findByToken(tokenHash);
        if (resetToken == null || !resetToken.isValid()) {
            log.warn("비밀번호 재설정 실패: 유효하지 않은 토큰");
            return false;
        }

        QrManageShopAdmin shopAdmin = shopAdminMapper.findById(resetToken.getShopAdminId());
        if (shopAdmin == null) {
            log.error("❌ [ERROR] 비밀번호 재설정 실패: shopAdminId={} 계정 없음", resetToken.getShopAdminId());
            return false;
        }

        shopAdmin.setPassword(passwordEncoder.encode(newPassword));
        shopAdminMapper.update(shopAdmin);

        tokenMapper.markAsUsed(tokenHash);

        log.info("✅ [CHECK] 비밀번호 재설정 완료: shopAdminId={}", shopAdmin.getId());
        return true;
    }

    /**
     * 토큰으로 점포 관리자 조회
     *
     * @param token 원문 토큰
     * @return 점포 관리자 정보
     */
    public QrManageShopAdmin findShopAdminByToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        QrManagePasswordResetToken resetToken = tokenMapper.findByToken(hashResetToken(token));
        if (resetToken == null || !resetToken.isValid()) {
            return null;
        }
        return shopAdminMapper.findById(resetToken.getShopAdminId());
    }

    private String hashResetToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 해시 알고리즘을 사용할 수 없습니다.", e);
        }
    }

    private String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return "";
        }

        int atIndex = email.indexOf('@');
        if (atIndex < 0) {
            return "***";
        }

        String domain = email.substring(atIndex);
        if (atIndex <= 1) {
            return "***" + domain;
        }
        return email.charAt(0) + "***" + domain;
    }
}
