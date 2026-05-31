package co.grap.pack.qrmanage.common.notification.mapper;

import co.grap.pack.qrmanage.common.notification.model.QrManageNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 알림 Mapper
 */
@Mapper
public interface QrManageNotificationMapper {

    /**
     * 알림 등록
     */
    void insert(QrManageNotification notification);

    /**
     * 최고관리자 알림 목록 조회
     */
    List<QrManageNotification> findBySuperAdmin(
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * 상점관리자 알림 목록 조회
     */
    List<QrManageNotification> findByShopAdminId(
            @Param("shopAdminId") Long shopAdminId,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * 최고관리자 읽지 않은 알림 수
     */
    int countUnreadBySuperAdmin();

    /**
     * 상점관리자 읽지 않은 알림 수
     */
    int countUnreadByShopAdminId(@Param("shopAdminId") Long shopAdminId);

    /**
     * 알림 읽음 처리
     */
    void markAsRead(@Param("id") Long id);

    /**
     * 전체 알림 읽음 처리 (최고관리자)
     */
    void markAllAsReadBySuperAdmin();

    /**
     * 전체 알림 읽음 처리 (상점관리자)
     */
    void markAllAsReadByShopAdminId(@Param("shopAdminId") Long shopAdminId);

    /**
     * 알림 삭제
     */
    void delete(@Param("id") Long id);

    /**
     * 오래된 알림 삭제 (30일 이상)
     */
    void deleteOldNotifications();
}
