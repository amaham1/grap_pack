package co.grap.pack.qrmanage.superadmin.shop.service;

import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShop;
import co.grap.pack.qrmanage.shopadmin.shop.model.QrManageShopStatus;
import co.grap.pack.qrmanage.superadmin.shop.mapper.QrManageSuperShopMapper;
import co.grap.pack.qrmanage.superadmin.shop.model.QrManageShopMemo;
import co.grap.pack.qrmanage.superadmin.shop.model.QrManageShopReviewHistory;
import co.grap.pack.qrmanage.superadmin.shop.model.QrManageShopSearchParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 최고관리자용 상점 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrManageSuperShopService {

    private final QrManageSuperShopMapper shopMapper;

    /**
     * 상점 목록 조회
     */
    public List<QrManageShop> findAll(QrManageShopSearchParam param) {
        return shopMapper.findAll(param);
    }

    /**
     * 상점 총 개수
     */
    public int countAll(QrManageShopSearchParam param) {
        return shopMapper.countAll(param);
    }

    /**
     * ID로 상점 조회
     */
    public QrManageShop findById(Long id) {
        return shopMapper.findById(id);
    }

    /**
     * 상점 승인
     */
    @Transactional
    public void approve(Long shopId, Long reviewerId, String comment) {
        log.info("✅ [CHECK] 상점 승인: shopId={}", shopId);

        shopMapper.updateStatus(shopId, QrManageShopStatus.APPROVED);

        // 검수 이력 등록
        QrManageShopReviewHistory history = QrManageShopReviewHistory.builder()
                .shopId(shopId)
                .reviewerId(reviewerId)
                .action("APPROVED")
                .comment(comment)
                .build();
        shopMapper.insertReviewHistory(history);
    }

    /**
     * 상점 반려
     */
    @Transactional
    public void reject(Long shopId, Long reviewerId, String comment) {
        log.info("✅ [CHECK] 상점 반려: shopId={}", shopId);

        shopMapper.updateStatus(shopId, QrManageShopStatus.REJECTED);

        // 검수 이력 등록
        QrManageShopReviewHistory history = QrManageShopReviewHistory.builder()
                .shopId(shopId)
                .reviewerId(reviewerId)
                .action("REJECTED")
                .comment(comment)
                .build();
        shopMapper.insertReviewHistory(history);
    }

    /**
     * 상점 노출 설정
     */
    @Transactional
    public void setVisibility(Long shopId, Boolean isVisible) {
        log.info("✅ [CHECK] 상점 노출 설정: shopId={}, isVisible={}", shopId, isVisible);
        shopMapper.updateVisibility(shopId, isVisible);
    }

    /**
     * 상점의 검수 이력 조회
     */
    public List<QrManageShopReviewHistory> findReviewHistory(Long shopId) {
        return shopMapper.findReviewHistoryByShopId(shopId);
    }

    /**
     * 메모 등록
     */
    @Transactional
    public void addMemo(Long shopId, Long adminId, String content) {
        log.info("✅ [CHECK] 상점 메모 등록: shopId={}", shopId);

        QrManageShopMemo memo = QrManageShopMemo.builder()
                .shopId(shopId)
                .adminId(adminId)
                .content(content)
                .build();
        shopMapper.insertMemo(memo);
    }

    /**
     * 상점의 메모 목록 조회
     */
    public List<QrManageShopMemo> findMemos(Long shopId) {
        return shopMapper.findMemosByShopId(shopId);
    }

    /**
     * 메모 삭제
     */
    @Transactional
    public void deleteMemo(Long memoId) {
        shopMapper.deleteMemo(memoId);
    }

    /**
     * 상태별 상점 수
     */
    public int countByStatus(QrManageShopStatus status) {
        return shopMapper.countByStatus(status);
    }

    /**
     * 검수 대기 상점 수
     */
    public int countPending() {
        return countByStatus(QrManageShopStatus.PENDING);
    }

    /**
     * 전체 상점 수
     */
    public int countTotal() {
        return shopMapper.countAll(new QrManageShopSearchParam());
    }
}
