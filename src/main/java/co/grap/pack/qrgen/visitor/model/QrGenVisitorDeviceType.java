package co.grap.pack.qrgen.visitor.model;

/**
 * 방문자 디바이스 타입 Enum
 */
public enum QrGenVisitorDeviceType {
    DESKTOP("데스크톱"),
    MOBILE("모바일"),
    TABLET("태블릿"),
    BOT("봇/크롤러"),
    UNKNOWN("알 수 없음");

    private final String displayName;

    QrGenVisitorDeviceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
