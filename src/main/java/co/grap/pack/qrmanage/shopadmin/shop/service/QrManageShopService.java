package co.grap.pack.qrmanage.shopadmin.shop.service;

import co.grap.pack.qrmanage.shopadmin.shop.mapper.QrManageShopMapper;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageBusinessHours;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShop;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShopStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 상점 서비스 (상점관리자용)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrManageShopService {

    private final QrManageShopMapper shopMapper;

    /**
     * 상점관리자 ID로 상점 조회
     * @param shopAdminId 상점관리자 ID
     * @return 상점 정보
     */
    public QrManageShop findByShopAdminId(Long shopAdminId) {
        return shopMapper.findByShopAdminId(shopAdminId);
    }

    /**
     * ID로 상점 조회
     * @param id 상점 ID
     * @return 상점 정보
     */
    public QrManageShop findById(Long id) {
        return shopMapper.findById(id);
    }

    /**
     * 상점 등록
     * @param shop 상점 정보
     * @return 등록된 상점
     */
    @Transactional
    public QrManageShop create(QrManageShop shop) {
        log.info("✅ [CHECK] 상점 등록: shopAdminId={}, name={}", shop.getShopAdminId(), shop.getName());

        shop.setStatus(QrManageShopStatus.PENDING);
        shop.setIsVisible(false);
        shopMapper.insert(shop);

        log.info("✅ [CHECK] 상점 등록 완료: id={}", shop.getId());
        return shop;
    }

    /**
     * 상점 수정
     * @param shop 상점 정보
     * @return 수정된 상점
     */
    @Transactional
    public QrManageShop update(QrManageShop shop) {
        log.info("✅ [CHECK] 상점 수정: id={}", shop.getId());

        // 수정하면 다시 검수 대기 상태로 변경
        shop.setStatus(QrManageShopStatus.PENDING);
        shopMapper.update(shop);

        return shop;
    }

    /**
     * 상점 정보만 수정 (상태 변경 없이)
     * @param shop 상점 정보
     */
    @Transactional
    public void updateInfo(QrManageShop shop) {
        log.info("✅ [CHECK] 상점 정보 수정: id={}", shop.getId());
        shopMapper.update(shop);
    }

    // ===== 영업시간 관련 =====

    /**
     * 상점의 영업시간 목록 조회
     * @param shopId 상점 ID
     * @return 영업시간 목록
     */
    public List<QrManageBusinessHours> findBusinessHours(Long shopId) {
        return shopMapper.findBusinessHoursByShopId(shopId);
    }

    /**
     * 영업시간 저장
     * @param shopId 상점 ID
     * @param businessHoursList 영업시간 목록
     */
    @Transactional
    public void saveBusinessHours(Long shopId, List<QrManageBusinessHours> businessHoursList) {
        log.info("✅ [CHECK] 영업시간 저장: shopId={}", shopId);

        // 기존 영업시간 삭제
        shopMapper.deleteBusinessHoursByShopId(shopId);

        // 새 영업시간 저장
        for (QrManageBusinessHours hours : businessHoursList) {
            hours.setShopId(shopId);
            shopMapper.upsertBusinessHours(hours);
        }
    }

    /**
     * 상점관리자 이메일로 상점 ID 조회
     * @param email 상점관리자 이메일
     * @return 상점 ID (없으면 null)
     */
    public Long getShopIdByAdminEmail(String email) {
        return shopMapper.findShopIdByAdminEmail(email);
    }

    /**
     * 상점관리자 username으로 상점 ID 조회
     * @param username 상점관리자 username
     * @return 상점 ID (없으면 null)
     */
    public Long getShopIdByAdminUsername(String username) {
        return shopMapper.findShopIdByAdminUsername(username);
    }

    /**
     * 상점관리자 이메일로 상점 정보 조회
     * @param email 상점관리자 이메일
     * @return 상점 정보 (없으면 null)
     */
    public QrManageShop getShopByAdminEmail(String email) {
        return shopMapper.findByAdminEmail(email);
    }

    /**
     * 상점관리자 username으로 상점 정보 조회
     * @param username 상점관리자 username
     * @return 상점 정보 (없으면 null)
     */
    public QrManageShop getShopByAdminUsername(String username) {
        return shopMapper.findByAdminUsername(username);
    }
}
