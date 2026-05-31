package co.grap.pack.qrmanage.common.log.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 활동 로그 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrManageActivityLog {

    /** 로그 ID */
    private Long id;

    /** 사용자 유형 (SUPER_ADMIN, SHOP_ADMIN) */
    private String userType;

    /** 사용자 ID */
    private Long userId;

    /** 사용자 이메일 */
    private String userEmail;

    /** 활동 유형 */
    private String activityType;

    /** 대상 유형 (SHOP, MENU, QR_CODE 등) */
    private String targetType;

    /** 대상 ID */
    private Long targetId;

    /** 활동 설명 */
    private String description;

    /** IP 주소 */
    private String ipAddress;

    /** 생성일시 */
    private LocalDateTime createdAt;
}
