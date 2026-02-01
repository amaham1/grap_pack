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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QrGenUser user = userMapper.findByUsername(username);

        if (user == null) {
            log.warn("사용자를 찾을 수 없습니다: {}", username);
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        if (!user.getQrGenUserIsActive()) {
            log.warn("비활성화된 사용자입니다: {}", username);
            throw new UsernameNotFoundException("비활성화된 계정입니다.");
        }

        // 마지막 로그인 시간 업데이트
        userMapper.updateLastLoginAt(user.getQrGenUserId());

        return new User(
            user.getQrGenUserUsername(),
            user.getQrGenUserPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_QRGEN_USER"))
        );
    }

    /**
     * 사용자명으로 사용자 조회
     */
    public QrGenUser findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    /**
     * ID로 사용자 조회
     */
    public QrGenUser findById(Long id) {
        return userMapper.findById(id);
    }

    /**
     * 회원가입
     */
    @Transactional
    public QrGenUser register(String username, String password, String email, String nickname) {
        // 중복 확인
        if (userMapper.countByUsername(username) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 사용자명입니다.");
        }
        if (userMapper.countByEmail(email) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        QrGenUser user = QrGenUser.builder()
            .qrGenUserUsername(username)
            .qrGenUserPassword(passwordEncoder.encode(password))
            .qrGenUserEmail(email)
            .qrGenUserNickname(nickname)
            .qrGenUserIsActive(true)
            .build();

        userMapper.insert(user);
        log.info("✅ [CHECK] 회원가입 완료: username={}", username);

        return user;
    }

    /**
     * 사용자명 중복 확인
     */
    public boolean isUsernameAvailable(String username) {
        return userMapper.countByUsername(username) == 0;
    }

    /**
     * 이메일 중복 확인
     */
    public boolean isEmailAvailable(String email) {
        return userMapper.countByEmail(email) == 0;
    }
}
