package co.grap.pack.admin.content.model;

import java.util.Arrays;

/**
 * 통합 콘텐츠 데이터셋 유형이다.
 */
public enum AdminDatasetType {

    FESTIVALS("festivals", "축제/행사", true, true, true, true),
    EXHIBITIONS("exhibitions", "전시/공연", true, true, true, true),
    WELFARE("welfare", "복지 서비스", true, true, true, true),
    GAS_STATIONS("gas-stations", "주유소", false, false, false, true),
    REAL_ESTATE("real-estate", "부동산", false, false, false, false);

    private final String pathSegment;
    private final String displayName;
    private final boolean supportsVisibility;
    private final boolean supportsMemo;
    private final boolean supportsConfirm;
    private final boolean supportsDelete;

    AdminDatasetType(
            String pathSegment,
            String displayName,
            boolean supportsVisibility,
            boolean supportsMemo,
            boolean supportsConfirm,
            boolean supportsDelete
    ) {
        this.pathSegment = pathSegment;
        this.displayName = displayName;
        this.supportsVisibility = supportsVisibility;
        this.supportsMemo = supportsMemo;
        this.supportsConfirm = supportsConfirm;
        this.supportsDelete = supportsDelete;
    }

    public String getPathSegment() {
        return pathSegment;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isSupportsVisibility() {
        return supportsVisibility;
    }

    public boolean isSupportsMemo() {
        return supportsMemo;
    }

    public boolean isSupportsConfirm() {
        return supportsConfirm;
    }

    public boolean isSupportsDelete() {
        return supportsDelete;
    }

    /**
     * 경로 세그먼트로 데이터셋 유형을 찾는다.
     */
    public static AdminDatasetType fromPath(String pathSegment) {
        return Arrays.stream(values())
                .filter(value -> value.pathSegment.equals(pathSegment))
                .findFirst()
                .orElse(null);
    }
}
