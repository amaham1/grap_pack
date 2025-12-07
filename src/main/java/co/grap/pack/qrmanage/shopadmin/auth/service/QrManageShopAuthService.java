package co.grap.pack.qrmanage.shopadmin.auth.service;

import co.grap.pack.qrmanage.shopadmin.auth.mapper.QrManageShopAdminMapper;
import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdmin;
import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdminStatus;
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
import java.util.List;

/**
 * QR 관리 서비스 상점관리자 인증 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrManageShopAuthService implements UserDetailsService {

    private final QrManageShopAdminMapper shopAdminMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("✅ [CHECK] 상점관리자 로그인 시도: {}", username);

        QrManageShopAdmin shopAdmin = shopAdminMapper.findByUsername(username);
        if (shopAdmin == null) {
            log.warn("❌ [ERROR] 상점관리자를 찾을 수 없음: {}", username);
            throw new UsernameNotFoundException("상점관리자를 찾을 수 없습니다: " + username);
        }

        // 계정 상태 확인
        if (shopAdmin.getStatus() != QrManageShopAdminStatus.ACTIVE) {
            log.warn("❌ [ERROR] 상점관리자 계정 상태 이상: {} - {}", username, shopAdmin.getStatus());
            throw new UsernameNotFoundException("계정이 비활성화 상태입니다: " + shopAdmin.getStatus().getDescription());
        }

        log.info("✅ [CHECK] 상점관리자 인증 성공: {}", username);

        return new User(
            shopAdmin.getUsername(),
            shopAdmin.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_SHOP_ADMIN"))
        );
    }

    /**
     * 상점관리자 회원가입
     * @param shopAdmin 상점관리자 정보
     * @return 등록된 상점관리자
     */
    @Transactional
    public QrManageShopAdmin register(QrManageShopAdmin shopAdmin) {
        log.info("✅ [CHECK] 상점관리자 회원가입: {}", shopAdmin.getUsername());

        // 중복 확인
        if (shopAdminMapper.existsByUsername(shopAdmin.getUsername())) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        if (shopAdminMapper.existsByEmail(shopAdmin.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        // 비밀번호 암호화
        shopAdmin.setPassword(passwordEncoder.encode(shopAdmin.getPassword()));
        // 초기 상태: 승인대기
        shopAdmin.setStatus(QrManageShopAdminStatus.PENDING);

        shopAdminMapper.insert(shopAdmin);
        log.info("✅ [CHECK] 상점관리자 회원가입 완료: {}", shopAdmin.getUsername());

        return shopAdmin;
    }

    /**
     * 사용자명으로 상점관리자 조회
     * @param username 사용자명
     * @return 상점관리자 정보
     */
    public QrManageShopAdmin findByUsername(String username) {
        return shopAdminMapper.findByUsername(username);
    }

    /**
     * ID로 상점관리자 조회
     * @param id 상점관리자 ID
     * @return 상점관리자 정보
     */
    public QrManageShopAdmin findById(Long id) {
        return shopAdminMapper.findById(id);
    }

    /**
     * 이메일로 상점관리자 조회
     * @param email 이메일
     * @return 상점관리자 정보
     */
    public QrManageShopAdmin findByEmail(String email) {
        return shopAdminMapper.findByEmail(email);
    }

    /**
     * 상점관리자 목록 조회
     * @param status 상태 필터
     * @param keyword 검색어
     * @return 상점관리자 목록
     */
    public List<QrManageShopAdmin> findAll(QrManageShopAdminStatus status, String keyword) {
        return shopAdminMapper.findAll(status, keyword);
    }

    /**
     * 상점관리자 상태 변경
     * @param id 상점관리자 ID
     * @param status 변경할 상태
     */
    @Transactional
    public void updateStatus(Long id, QrManageShopAdminStatus status) {
        log.info("✅ [CHECK] 상점관리자 상태 변경: id={}, status={}", id, status);
        shopAdminMapper.updateStatus(id, status);
    }

    /**
     * 비밀번호 변경
     * @param id 상점관리자 ID
     * @param newPassword 새 비밀번호
     */
    @Transactional
    public void changePassword(Long id, String newPassword) {
        QrManageShopAdmin shopAdmin = shopAdminMapper.findById(id);
        if (shopAdmin == null) {
            throw new IllegalArgumentException("상점관리자를 찾을 수 없습니다.");
        }
        shopAdmin.setPassword(passwordEncoder.encode(newPassword));
        shopAdminMapper.update(shopAdmin);
        log.info("✅ [CHECK] 상점관리자 비밀번호 변경 완료: id={}", id);
    }

    /**
     * 사용자명 중복 확인
     * @param username 사용자명
     * @return 중복이면 true
     */
    public boolean existsByUsername(String username) {
        return shopAdminMapper.existsByUsername(username);
    }

    /**
     * 이메일 중복 확인
     * @param email 이메일
     * @return 중복이면 true
     */
    public boolean existsByEmail(String email) {
        return shopAdminMapper.existsByEmail(email);
    }
}
