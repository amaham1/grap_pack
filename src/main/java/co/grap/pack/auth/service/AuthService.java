package co.grap.pack.auth.service;

import co.grap.pack.auth.mapper.AuthMapper;
import co.grap.pack.auth.model.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * 인증 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService implements UserDetailsService {

    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Spring Security 인증을 위한 사용자 정보 로드
     * @param username 사용자명
     * @return 사용자 상세 정보
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = authMapper.selectAdminByUsername(username);

        if (admin == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        return User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .authorities(new ArrayList<>())
                .build();
    }

    /**
     * 관리자 등록
     * @param admin 관리자 정보
     */
    @Transactional
    public void createAdmin(Admin admin) {
        // 비밀번호 암호화
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        authMapper.insertAdmin(admin);
    }

    /**
     * 사용자명으로 관리자 조회
     * @param username 사용자명
     * @return 관리자 정보
     */
    public Admin getAdminByUsername(String username) {
        return authMapper.selectAdminByUsername(username);
    }
}
