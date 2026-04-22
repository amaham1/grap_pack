package co.grap.pack.admin.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * 통합 운영 포털 기본 테이블과 운영자 마이그레이션을 준비한다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminOperatorMigrationService implements ApplicationRunner {

    @Qualifier("grapDataSource")
    private final DataSource grapDataSource;

    @Override
    public void run(ApplicationArguments args) {
        try {
            ensureAdminOperatorTable();
            ensureAdminActionLogTable();
            migrateLegacySuperAdmins();
        } catch (Exception exception) {
            log.error("❌ [ERROR] 통합 운영 포털 초기화 실패: {}", exception.getMessage(), exception);
        }
    }

    /**
     * 운영자 원본 테이블을 준비한다.
     */
    public void ensureAdminOperatorTable() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(grapDataSource);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS admin_operator (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '통합 운영자 ID',
                    login_id VARCHAR(50) NOT NULL UNIQUE COMMENT '로그인 ID',
                    password VARCHAR(255) NOT NULL COMMENT '비밀번호',
                    name VARCHAR(100) NOT NULL COMMENT '이름',
                    email VARCHAR(100) NULL COMMENT '이메일',
                    role VARCHAR(30) NOT NULL COMMENT '권한 역할',
                    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성 여부',
                    last_login_at DATETIME NULL COMMENT '마지막 로그인 시각',
                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시각',
                    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시각',
                    INDEX idx_admin_operator_role (role),
                    INDEX idx_admin_operator_is_active (is_active)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='통합 운영 포털 운영자'
                """);

        log.info("✅ [CHECK] admin_operator 테이블 준비 완료");
    }

    /**
     * 운영자 액션 로그 테이블을 준비한다.
     */
    public void ensureAdminActionLogTable() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(grapDataSource);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS admin_action_log (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '운영 액션 로그 ID',
                    operator_id BIGINT NOT NULL COMMENT '운영자 ID',
                    domain_code VARCHAR(30) NOT NULL COMMENT '도메인 코드',
                    action_code VARCHAR(50) NOT NULL COMMENT '액션 코드',
                    target_type VARCHAR(50) NULL COMMENT '대상 타입',
                    target_id BIGINT NULL COMMENT '대상 ID',
                    summary VARCHAR(500) NOT NULL COMMENT '요약 설명',
                    ip_address VARCHAR(45) NULL COMMENT 'IP 주소',
                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시각',
                    INDEX idx_admin_action_log_operator_id (operator_id),
                    INDEX idx_admin_action_log_domain_code (domain_code),
                    INDEX idx_admin_action_log_action_code (action_code),
                    INDEX idx_admin_action_log_created_at (created_at)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='통합 운영 포털 액션 로그'
                """);

        log.info("✅ [CHECK] admin_action_log 테이블 준비 완료");
    }

    /**
     * 기존 QR 슈퍼 관리자를 통합 운영자로 이관한다.
     */
    public void migrateLegacySuperAdmins() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(grapDataSource);
        if (!tableExists(jdbcTemplate, "qr_manage_super_admin")) {
            log.info("ℹ️ [CHECK] qr_manage_super_admin 테이블이 없어 운영자 이관을 건너뜁니다.");
            return;
        }

        List<Map<String, Object>> legacySuperAdmins = jdbcTemplate.queryForList("""
                SELECT id, username, password, name, email, created_at, updated_at
                FROM qr_manage_super_admin
                ORDER BY id ASC
                """);

        int migratedCount = 0;
        for (Map<String, Object> row : legacySuperAdmins) {
            jdbcTemplate.update("""
                            INSERT INTO admin_operator (
                                login_id,
                                password,
                                name,
                                email,
                                role,
                                is_active,
                                created_at,
                                updated_at
                            ) VALUES (?, ?, ?, ?, 'SUPER_ADMIN', TRUE, COALESCE(?, NOW()), COALESCE(?, NOW()))
                            ON DUPLICATE KEY UPDATE
                                password = VALUES(password),
                                name = VALUES(name),
                                email = VALUES(email),
                                role = VALUES(role),
                                is_active = TRUE,
                                updated_at = NOW()
                            """,
                    stringValue(row.get("username")),
                    stringValue(row.get("password")),
                    stringValue(row.get("name")),
                    stringValue(row.get("email")),
                    row.get("created_at"),
                    row.get("updated_at")
            );
            migratedCount++;
        }

        log.info("✅ [CHECK] 통합 운영자 이관 완료: migratedCount={}", migratedCount);
    }

    private boolean tableExists(JdbcTemplate jdbcTemplate, String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM information_schema.tables
                        WHERE table_schema = DATABASE()
                          AND table_name = ?
                        """,
                Integer.class,
                tableName
        );
        return count != null && count > 0;
    }

    private String stringValue(Object value) {
        return value != null ? value.toString() : null;
    }
}
