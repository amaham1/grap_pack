package co.grap.pack.qrgen.auth.service;

import co.grap.pack.qrgen.auth.mapper.QrGenUserMapper;
import co.grap.pack.qrgen.auth.model.QrGenUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * QR Generator 인증 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrGenAuthService implements UserDetailsService {

    private final QrGenUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Spring Security UserDetailsService 구현
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        QrGenUser user = userMapper.findQrGenUserByLoginId(loginId);

        if (user == null) {
            log.warn("사용자를 찾을 수 없습니다: {}", loginId);
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + loginId);
        }

        if (!user.getQrGenUserIsActive()) {
            log.warn("비활성화된 사용자입니다: {}", loginId);
            throw new UsernameNotFoundException("비활성화된 계정입니다.");
        }

        // 마지막 로그인 시간 업데이트
        userMapper.updateQrGenUserLastLoginAt(user.getQrGenUserId());

        return new User(
            user.getQrGenUserLoginId(),
            user.getQrGenUserPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_QRGEN_USER"))
        );
    }

    /**
     * 로그인 ID로 사용자 조회
     */
    public QrGenUser findQrGenUserByLoginId(String loginId) {
        return userMapper.findQrGenUserByLoginId(loginId);
    }

    /**
     * ID로 사용자 조회
     */
    public QrGenUser findQrGenUserById(Long id) {
        return userMapper.findQrGenUserById(id);
    }

    /**
     * 회원가입
     */
    @Transactional
    public QrGenUser registerQrGenUser(String loginId, String password, String nickname) {
        // 중복 확인
        if (userMapper.countQrGenUserByLoginId(loginId) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        QrGenUser user = QrGenUser.builder()
            .qrGenUserLoginId(loginId)
            .qrGenUserPassword(passwordEncoder.encode(password))
            .qrGenUserNickname(nickname)
            .qrGenUserIsActive(true)
            .build();

        userMapper.insertQrGenUser(user);
        log.info("✅ [CHECK] 회원가입 완료: loginId={}", loginId);

        return user;
    }

    /**
     * 로그인 ID 중복 확인
     */
    public boolean isQrGenUserLoginIdAvailable(String loginId) {
        return userMapper.countQrGenUserByLoginId(loginId) == 0;
    }

    /**
     * 이메일 중복 확인
     */
    public boolean isQrGenUserEmailAvailable(String email) {
        return userMapper.countQrGenUserByEmail(email) == 0;
    }
}
