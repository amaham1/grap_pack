package co.grap.pack.qrmanage.shopadmin.auth.model;

/**
 * 상점관리자 계정 상태
 */
public enum QrManageShopAdminStatus {

    /** 승인 대기 (회원가입 후 최고관리자 승인 전) */
    PENDING("승인대기"),

    /** 활성 (정상 이용 가능) */
    ACTIVE("활성"),

    /** 비활성 (본인이 비활성화) */
    INACTIVE("비활성"),

    /** 정지 (최고관리자에 의해 정지) */
    SUSPENDED("정지");

    private final String description;

    QrManageShopAdminStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
