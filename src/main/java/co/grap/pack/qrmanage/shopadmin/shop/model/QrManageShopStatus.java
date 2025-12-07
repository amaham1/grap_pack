package co.grap.pack.qrmanage.shopadmin.shop.model;

/**
 * 상점 상태
 */
public enum QrManageShopStatus {

    /** 검수 대기 */
    PENDING("검수대기"),

    /** 승인됨 */
    APPROVED("승인"),

    /** 반려됨 */
    REJECTED("반려");

    private final String description;

    QrManageShopStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
