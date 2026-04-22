package co.grap.pack.admin.auth.service;

import co.grap.pack.admin.auth.mapper.AdminOperatorMapper;
import co.grap.pack.admin.auth.model.AdminOperator;
import co.grap.pack.admin.auth.model.AdminSessionPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * 통합 운영자 인증 서비스다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminOperatorService implements UserDetailsService {

    private final AdminOperatorMapper adminOperatorMapper;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        AdminOperator adminOperator = adminOperatorMapper.findByLoginId(loginId);
        if (adminOperator == null) {
            log.warn("❌ [ERROR] 통합 운영자를 찾을 수 없습니다: {}", loginId);
            throw new UsernameNotFoundException("통합 운영자를 찾을 수 없습니다: " + loginId);
        }

        if (!Boolean.TRUE.equals(adminOperator.getIsActive())) {
            log.warn("❌ [ERROR] 비활성 통합 운영자 로그인 차단: {}", loginId);
            throw new UsernameNotFoundException("비활성 운영자 계정입니다.");
        }

        adminOperatorMapper.updateLastLoginAt(adminOperator.getId());
        log.info("✅ [CHECK] 통합 운영자 로그인 준비 완료: {}", loginId);

        return AdminSessionPrincipal.builder()
                .id(adminOperator.getId())
                .loginId(adminOperator.getLoginId())
                .password(adminOperator.getPassword())
                .name(adminOperator.getName())
                .email(adminOperator.getEmail())
                .role(adminOperator.getRole())
                .active(adminOperator.getIsActive())
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + adminOperator.getRole().name())
                ))
                .build();
    }

    /**
     * 로그인 ID로 운영자를 조회한다.
     */
    public AdminOperator findByLoginId(String loginId) {
        return adminOperatorMapper.findByLoginId(loginId);
    }
}
