package co.grap.pack.qrmanage.superadmin.auth.service;

import co.grap.pack.qrmanage.superadmin.auth.mapper.QrManageSuperAdminMapper;
import co.grap.pack.qrmanage.superadmin.auth.model.QrManageSuperAdmin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * QR 관리 서비스 최고관리자 인증 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrManageSuperAuthService implements UserDetailsService {

    private final QrManageSuperAdminMapper superAdminMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("✅ [CHECK] 최고관리자 로그인 시도: {}", username);

        QrManageSuperAdmin superAdmin = superAdminMapper.findByUsername(username);
        if (superAdmin == null) {
            log.warn("❌ [ERROR] 최고관리자를 찾을 수 없음: {}", username);
            throw new UsernameNotFoundException("최고관리자를 찾을 수 없습니다: " + username);
        }

        log.info("✅ [CHECK] 최고관리자 인증 성공: {}", username);

        return new User(
            superAdmin.getUsername(),
            superAdmin.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"))
        );
    }

    /**
     * 사용자명으로 최고관리자 조회
     * @param username 사용자명
     * @return 최고관리자 정보
     */
    public QrManageSuperAdmin findByUsername(String username) {
        return superAdminMapper.findByUsername(username);
    }

    /**
     * ID로 최고관리자 조회
     * @param id 최고관리자 ID
     * @return 최고관리자 정보
     */
    public QrManageSuperAdmin findById(Long id) {
        return superAdminMapper.findById(id);
    }
}
