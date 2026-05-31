package co.grap.pack.qrmanage.common.notification.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 알림 유형 열거형
 */
@Getter
@RequiredArgsConstructor
public enum QrManageNotificationType {

    // 최고관리자 수신 알림
    NEW_SHOP_ADMIN("새 상점관리자 가입", "새로운 상점관리자가 가입 승인을 요청했습니다."),
    NEW_SHOP_REGISTER("새 상점 등록", "새로운 상점이 등록되어 검수가 필요합니다."),

    // 상점관리자 수신 알림
    ACCOUNT_APPROVED("계정 승인", "계정이 승인되었습니다."),
    ACCOUNT_REJECTED("계정 반려", "계정 승인이 반려되었습니다."),
    SHOP_APPROVED("상점 승인", "상점이 승인되어 서비스가 시작됩니다."),
    SHOP_REJECTED("상점 반려", "상점 검수가 반려되었습니다."),
    QR_EXPIRED("QR 만료 예정", "QR 코드가 곧 만료됩니다."),

    // 공통
    SYSTEM_NOTICE("시스템 공지", "시스템 공지사항입니다.");

    private final String title;
    private final String defaultMessage;
}
