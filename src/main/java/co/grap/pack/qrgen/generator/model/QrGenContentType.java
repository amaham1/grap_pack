package co.grap.pack.qrgen.generator.model;

/**
 * QR 코드 콘텐츠 타입
 */
public enum QrGenContentType {

    URL("URL"),
    TEXT("텍스트"),
    VCARD("연락처"),
    WIFI("WiFi"),
    EMAIL("이메일"),
    SMS("SMS"),
    PHONE("전화번호"),
    GEO("위치");

    private final String description;

    QrGenContentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
