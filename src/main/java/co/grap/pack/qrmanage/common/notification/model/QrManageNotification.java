package co.grap.pack.qrmanage.common.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 알림 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageNotification {

    /** 알림 ID */
    private Long id;

    /** 수신자 타입 (SUPER_ADMIN, SHOP_ADMIN) */
    private String recipientType;

    /** 수신자 ID (상점관리자 ID 또는 null) */
    private Long recipientId;

    /** 알림 유형 */
    private QrManageNotificationType notificationType;

    /** 알림 제목 */
    private String title;

    /** 알림 내용 */
    private String message;

    /** 관련 링크 */
    private String linkUrl;

    /** 읽음 여부 */
    private Boolean isRead;

    /** 생성일시 */
    private LocalDateTime createdAt;

    /** 읽은 일시 */
    private LocalDateTime readAt;
}
