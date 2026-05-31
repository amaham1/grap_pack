package co.grap.pack.qrmanage.shopadmin.auth.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import co.grap.pack.qrmanage.shopadmin.auth.mapper.QrManagePasswordResetTokenMapper;
import co.grap.pack.qrmanage.shopadmin.auth.mapper.QrManageShopAdminMapper;
import co.grap.pack.qrmanage.shopadmin.auth.model.QrManagePasswordResetToken;
import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QrManagePasswordResetServiceTest {

    @Mock
    private QrManagePasswordResetTokenMapper tokenMapper;

    @Mock
    private QrManageShopAdminMapper shopAdminMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private QrManagePasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        passwordResetService = new QrManagePasswordResetService(tokenMapper, shopAdminMapper, passwordEncoder);
    }

    @Test
    void createResetTokenStoresHashInsteadOfRawToken() {
        when(shopAdminMapper.findByEmail("owner@example.com"))
                .thenReturn(QrManageShopAdmin.builder().id(5L).build());

        String rawToken = passwordResetService.createResetToken("owner@example.com");

        ArgumentCaptor<QrManagePasswordResetToken> tokenCaptor =
                ArgumentCaptor.forClass(QrManagePasswordResetToken.class);
        verify(tokenMapper).insert(tokenCaptor.capture());

        QrManagePasswordResetToken savedToken = tokenCaptor.getValue();
        assertThat(rawToken).isNotBlank();
        assertThat(savedToken.getToken()).isNotEqualTo(rawToken);
        assertThat(savedToken.getToken()).isEqualTo(sha256(rawToken));
        assertThat(savedToken.getToken()).hasSize(64);
        assertThat(savedToken.getUsed()).isFalse();
        verify(tokenMapper).invalidateByShopAdminId(5L);
    }

    @Test
    void validateTokenFindsTokenByHash() {
        String rawToken = "raw-reset-token";
        String tokenHash = sha256(rawToken);
        when(tokenMapper.findByToken(tokenHash)).thenReturn(validToken());

        assertThat(passwordResetService.validateToken(rawToken)).isTrue();

        verify(tokenMapper).findByToken(tokenHash);
    }

    @Test
    void validateTokenRejectsRandomOrLegacyRawToken() {
        String rawToken = "legacy-raw-token";
        String tokenHash = sha256(rawToken);
        when(tokenMapper.findByToken(tokenHash)).thenReturn(null);

        assertThat(passwordResetService.validateToken(rawToken)).isFalse();

        verify(tokenMapper).findByToken(tokenHash);
    }

    @Test
    void resetPasswordMarksHashedTokenAsUsed() {
        String rawToken = "reset-token";
        String tokenHash = sha256(rawToken);
        QrManageShopAdmin shopAdmin = QrManageShopAdmin.builder().id(5L).password("old").build();

        when(tokenMapper.findByToken(tokenHash)).thenReturn(validToken());
        when(shopAdminMapper.findById(5L)).thenReturn(shopAdmin);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-password");

        assertThat(passwordResetService.resetPassword(rawToken, "new-password")).isTrue();

        assertThat(shopAdmin.getPassword()).isEqualTo("encoded-password");
        verify(shopAdminMapper).update(shopAdmin);
        verify(tokenMapper).markAsUsed(tokenHash);
    }

    @Test
    void validateTokenDoesNotExposeRawTokenInLogs() {
        String rawToken = "sensitive-reset-token";
        when(tokenMapper.findByToken(sha256(rawToken))).thenReturn(null);

        Logger logger = (Logger) LoggerFactory.getLogger(QrManagePasswordResetService.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            passwordResetService.validateToken(rawToken);
        } finally {
            logger.detachAppender(appender);
        }

        assertThat(appender.list)
                .noneMatch(event -> event.getFormattedMessage().contains(rawToken));
    }

    private QrManagePasswordResetToken validToken() {
        return QrManagePasswordResetToken.builder()
                .id(1L)
                .shopAdminId(5L)
                .token("stored-hash")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
