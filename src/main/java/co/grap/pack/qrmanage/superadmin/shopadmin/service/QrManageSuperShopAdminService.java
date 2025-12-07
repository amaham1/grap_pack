package co.grap.pack.qrmanage.superadmin.shopadmin.service;

import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdmin;
import co.grap.pack.qrmanage.shopadmin.auth.model.QrManageShopAdminStatus;
import co.grap.pack.qrmanage.superadmin.shopadmin.mapper.QrManageSuperShopAdminMapper;
import co.grap.pack.qrmanage.superadmin.shopadmin.model.QrManageShopAdminSearchParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 최고관리자용 상점관리자 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrManageSuperShopAdminService {

    private final QrManageSuperShopAdminMapper shopAdminMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 상점관리자 목록 조회
     * @param param 검색 파라미터
     * @return 상점관리자 목록
     */
    public List<QrManageShopAdmin> findAll(QrManageShopAdminSearchParam param) {
        return shopAdminMapper.findAll(param);
    }

    /**
     * 상점관리자 총 개수
     * @param param 검색 파라미터
     * @return 총 개수
     */
    public int countAll(QrManageShopAdminSearchParam param) {
        return shopAdminMapper.countAll(param);
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
     * 상점관리자 승인
     * @param id 상점관리자 ID
     */
    @Transactional
    public void approve(Long id) {
        log.info("✅ [CHECK] 상점관리자 승인: id={}", id);
        shopAdminMapper.updateStatus(id, QrManageShopAdminStatus.ACTIVE);
    }

    /**
     * 상점관리자 정지
     * @param id 상점관리자 ID
     */
    @Transactional
    public void suspend(Long id) {
        log.info("✅ [CHECK] 상점관리자 정지: id={}", id);
        shopAdminMapper.updateStatus(id, QrManageShopAdminStatus.SUSPENDED);
    }

    /**
     * 상점관리자 활성화
     * @param id 상점관리자 ID
     */
    @Transactional
    public void activate(Long id) {
        log.info("✅ [CHECK] 상점관리자 활성화: id={}", id);
        shopAdminMapper.updateStatus(id, QrManageShopAdminStatus.ACTIVE);
    }

    /**
     * 상점관리자 비활성화
     * @param id 상점관리자 ID
     */
    @Transactional
    public void deactivate(Long id) {
        log.info("✅ [CHECK] 상점관리자 비활성화: id={}", id);
        shopAdminMapper.updateStatus(id, QrManageShopAdminStatus.INACTIVE);
    }

    /**
     * 상점관리자 직접 생성 (최고관리자)
     * @param shopAdmin 상점관리자 정보
     * @return 생성된 상점관리자
     */
    @Transactional
    public QrManageShopAdmin create(QrManageShopAdmin shopAdmin) {
        log.info("✅ [CHECK] 상점관리자 직접 생성: {}", shopAdmin.getUsername());

        // 비밀번호 암호화
        shopAdmin.setPassword(passwordEncoder.encode(shopAdmin.getPassword()));
        // 최고관리자가 직접 생성하면 바로 활성 상태
        shopAdmin.setStatus(QrManageShopAdminStatus.ACTIVE);

        shopAdminMapper.insert(shopAdmin);
        return shopAdmin;
    }

    /**
     * 상태별 상점관리자 수 조회
     * @param status 상태
     * @return 개수
     */
    public int countByStatus(QrManageShopAdminStatus status) {
        return shopAdminMapper.countByStatus(status);
    }

    /**
     * 승인 대기 중인 상점관리자 수
     * @return 개수
     */
    public int countPending() {
        return countByStatus(QrManageShopAdminStatus.PENDING);
    }

    /**
     * 전체 상점관리자 수
     * @return 개수
     */
    public int countTotal() {
        return shopAdminMapper.countAll(new QrManageShopAdminSearchParam());
    }
}
