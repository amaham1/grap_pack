package co.grap.pack.qrmanage.shopadmin.qrcode.model;

/**
 * QR 코드 타입
 */
public enum QrManageQrCodeType {

    /** 상점 대표 QR - 상점 정보 페이지로 이동 */
    SHOP("상점 대표 QR"),

    /** 메뉴 QR - 메뉴 목록 페이지로 이동 */
    MENU("메뉴 QR");

    private final String description;

    QrManageQrCodeType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
