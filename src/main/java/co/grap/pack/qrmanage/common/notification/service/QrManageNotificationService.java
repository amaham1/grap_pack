package co.grap.pack.qrmanage.common.notification.service;

import co.grap.pack.qrmanage.common.notification.mapper.QrManageNotificationMapper;
import co.grap.pack.qrmanage.common.notification.model.QrManageNotification;
import co.grap.pack.qrmanage.common.notification.model.QrManageNotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 알림 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QrManageNotificationService {

    private final QrManageNotificationMapper notificationMapper;

    /**
     * 최고관리자에게 알림 발송
     */
    @Transactional
    public void notifySuperAdmin(QrManageNotificationType type, String message, String linkUrl) {
        QrManageNotification notification = QrManageNotification.builder()
                .recipientType("SUPER_ADMIN")
                .recipientId(null)
                .notificationType(type)
                .title(type.getTitle())
                .message(message != null ? message : type.getDefaultMessage())
                .linkUrl(linkUrl)
                .isRead(false)
                .build();
        notificationMapper.insert(notification);
        log.info("✅ [CHECK] 최고관리자 알림 발송: type={}", type);
    }

    /**
     * 상점관리자에게 알림 발송
     */
    @Transactional
    public void notifyShopAdmin(Long shopAdminId, QrManageNotificationType type, String message, String linkUrl) {
        QrManageNotification notification = QrManageNotification.builder()
                .recipientType("SHOP_ADMIN")
                .recipientId(shopAdminId)
                .notificationType(type)
                .title(type.getTitle())
                .message(message != null ? message : type.getDefaultMessage())
                .linkUrl(linkUrl)
                .isRead(false)
                .build();
        notificationMapper.insert(notification);
        log.info("✅ [CHECK] 상점관리자 알림 발송: shopAdminId={}, type={}", shopAdminId, type);
    }

    /**
     * 최고관리자 알림 목록 조회
     */
    public List<QrManageNotification> getSuperAdminNotifications(int page, int size) {
        int offset = page * size;
        return notificationMapper.findBySuperAdmin(offset, size);
    }

    /**
     * 상점관리자 알림 목록 조회
     */
    public List<QrManageNotification> getShopAdminNotifications(Long shopAdminId, int page, int size) {
        int offset = page * size;
        return notificationMapper.findByShopAdminId(shopAdminId, offset, size);
    }

    /**
     * 최고관리자 읽지 않은 알림 수
     */
    public int getUnreadCountForSuperAdmin() {
        return notificationMapper.countUnreadBySuperAdmin();
    }

    /**
     * 상점관리자 읽지 않은 알림 수
     */
    public int getUnreadCountForShopAdmin(Long shopAdminId) {
        return notificationMapper.countUnreadByShopAdminId(shopAdminId);
    }

    /**
     * 알림 읽음 처리
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationMapper.markAsRead(notificationId);
    }

    /**
     * 전체 알림 읽음 처리 (최고관리자)
     */
    @Transactional
    public void markAllAsReadForSuperAdmin() {
        notificationMapper.markAllAsReadBySuperAdmin();
    }

    /**
     * 전체 알림 읽음 처리 (상점관리자)
     */
    @Transactional
    public void markAllAsReadForShopAdmin(Long shopAdminId) {
        notificationMapper.markAllAsReadByShopAdminId(shopAdminId);
    }

    /**
     * 알림 삭제
     */
    @Transactional
    public void delete(Long notificationId) {
        notificationMapper.delete(notificationId);
    }

    /**
     * 오래된 알림 정리 (30일 이상)
     */
    @Transactional
    public void cleanupOldNotifications() {
        notificationMapper.deleteOldNotifications();
        log.info("✅ [CHECK] 오래된 알림 정리 완료");
    }
}
