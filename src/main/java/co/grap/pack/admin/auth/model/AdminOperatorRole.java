package co.grap.pack.admin.auth.model;

/**
 * 통합 운영 포털 권한 역할이다.
 */
public enum AdminOperatorRole {

    SUPER_ADMIN("슈퍼 관리자");

    private final String displayName;

    AdminOperatorRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
