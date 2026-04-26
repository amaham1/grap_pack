package co.grap.pack.common.visitor.service;

import co.grap.pack.common.visitor.model.PackVisitorAuthScope;
import co.grap.pack.common.visitor.model.PackVisitorClassification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 공통 방문자 테이블 생성과 QRgen 레거시 데이터 이관을 담당한다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PackVisitorMigrationService implements ApplicationRunner {

    private static final String LEGACY_SOURCE = "QRGEN_LEGACY";

    private final PackVisitorRouteClassifier packVisitorRouteClassifier;

    @Qualifier("grapDataSource")
    private final DataSource grapDataSource;

    @Qualifier("qrGenDataSource")
    private final DataSource qrGenDataSource;

    @Override
    public void run(ApplicationArguments args) {
        try {
            ensurePackVisitorTable();
            migrateLegacyQrGenVisitors();
        } catch (Exception exception) {
            log.error("❌ [ERROR] 공통 방문자 초기화 실패: {}", exception.getMessage(), exception);
        }
    }

    /**
     * 공통 방문자 테이블을 준비한다.
     */
    public void ensurePackVisitorTable() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(grapDataSource);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS grap_pack_visitor (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '방문 ID',
                    session_id VARCHAR(100) NOT NULL COMMENT '세션 ID',
                    auth_scope VARCHAR(30) NOT NULL DEFAULT 'ANONYMOUS' COMMENT '인증 범위',
                    auth_user_id BIGINT NULL COMMENT '인증 사용자 ID',
                    service_code VARCHAR(30) NOT NULL COMMENT '서비스 코드',
                    menu_code VARCHAR(50) NOT NULL COMMENT '메뉴 코드',
                    route_key VARCHAR(255) NOT NULL COMMENT '정규화 라우트 키',
                    page_url VARCHAR(500) NOT NULL COMMENT '실제 방문 URL',
                    referrer VARCHAR(500) NULL COMMENT '리퍼러',
                    ip_address VARCHAR(45) NOT NULL COMMENT 'IP 주소',
                    user_agent TEXT NULL COMMENT 'User-Agent',
                    browser_name VARCHAR(50) NULL COMMENT '브라우저명',
                    browser_version VARCHAR(30) NULL COMMENT '브라우저 버전',
                    os_name VARCHAR(50) NULL COMMENT 'OS명',
                    os_version VARCHAR(30) NULL COMMENT 'OS 버전',
                    device_type VARCHAR(20) NOT NULL DEFAULT 'UNKNOWN' COMMENT '디바이스 유형',
                    screen_resolution VARCHAR(20) NULL COMMENT '화면 해상도',
                    language VARCHAR(10) NULL COMMENT '브라우저 언어',
                    visited_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '방문 시각',
                    duration_seconds INT NULL COMMENT '체류시간(초)',
                    visible_duration_seconds INT NOT NULL DEFAULT 0 COMMENT '화면이 실제로 보인 시간(초)',
                    interaction_count INT NOT NULL DEFAULT 0 COMMENT '사용자 상호작용 횟수',
                    first_interaction_at DATETIME NULL COMMENT '첫 사용자 상호작용 시각',
                    human_verified TINYINT(1) NOT NULL DEFAULT 0 COMMENT '사람 추정 방문 여부',
                    human_verified_at DATETIME NULL COMMENT '사람 추정 방문 확인 시각',
                    legacy_source VARCHAR(50) NULL COMMENT '레거시 소스',
                    legacy_source_id BIGINT NULL COMMENT '레거시 소스 ID',
                    INDEX idx_grap_pack_visitor_session_id (session_id),
                    INDEX idx_grap_pack_visitor_service_code (service_code),
                    INDEX idx_grap_pack_visitor_menu_code (menu_code),
                    INDEX idx_grap_pack_visitor_route_key (route_key),
                    INDEX idx_grap_pack_visitor_visited_at (visited_at),
                    INDEX idx_grap_pack_visitor_device_type (device_type),
                    INDEX idx_grap_pack_visitor_human_verified_at (human_verified, visited_at),
                    UNIQUE KEY uk_grap_pack_visitor_legacy (legacy_source, legacy_source_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='grap_pack 공통 방문자 추적'
                """);

        ensureColumn(jdbcTemplate, "visible_duration_seconds",
                "ALTER TABLE grap_pack_visitor ADD COLUMN visible_duration_seconds INT NOT NULL DEFAULT 0 COMMENT '화면이 실제로 보인 시간(초)' AFTER duration_seconds");
        ensureColumn(jdbcTemplate, "interaction_count",
                "ALTER TABLE grap_pack_visitor ADD COLUMN interaction_count INT NOT NULL DEFAULT 0 COMMENT '사용자 상호작용 횟수' AFTER visible_duration_seconds");
        ensureColumn(jdbcTemplate, "first_interaction_at",
                "ALTER TABLE grap_pack_visitor ADD COLUMN first_interaction_at DATETIME NULL COMMENT '첫 사용자 상호작용 시각' AFTER interaction_count");
        ensureColumn(jdbcTemplate, "human_verified",
                "ALTER TABLE grap_pack_visitor ADD COLUMN human_verified TINYINT(1) NOT NULL DEFAULT 0 COMMENT '사람 추정 방문 여부' AFTER first_interaction_at");
        ensureColumn(jdbcTemplate, "human_verified_at",
                "ALTER TABLE grap_pack_visitor ADD COLUMN human_verified_at DATETIME NULL COMMENT '사람 추정 방문 확인 시각' AFTER human_verified");
        ensureIndex(jdbcTemplate, "idx_grap_pack_visitor_human_verified_at",
                "CREATE INDEX idx_grap_pack_visitor_human_verified_at ON grap_pack_visitor (human_verified, visited_at)");

        log.info("✅ [CHECK] 공통 방문자 테이블 준비 완료");
    }

    /**
     * QRgen 레거시 방문자 데이터를 공통 테이블로 이관한다.
     */
    public void migrateLegacyQrGenVisitors() {
        JdbcTemplate sourceJdbcTemplate = new JdbcTemplate(qrGenDataSource);
        JdbcTemplate targetJdbcTemplate = new JdbcTemplate(grapDataSource);

        if (!tableExists(sourceJdbcTemplate, "qr_gen_visitor")) {
            log.info("ℹ️ [CHECK] QRgen 레거시 방문자 테이블이 없어 이관을 건너뜁니다.");
            return;
        }

        List<Map<String, Object>> legacyVisitors = sourceJdbcTemplate.queryForList("""
                SELECT
                    qr_gen_visitor_id,
                    qr_gen_visitor_session_id,
                    qr_gen_visitor_user_id,
                    qr_gen_visitor_ip_address,
                    qr_gen_visitor_user_agent,
                    qr_gen_visitor_page_url,
                    qr_gen_visitor_referrer,
                    qr_gen_visitor_browser_name,
                    qr_gen_visitor_browser_version,
                    qr_gen_visitor_os_name,
                    qr_gen_visitor_os_version,
                    qr_gen_visitor_device_type,
                    qr_gen_visitor_screen_resolution,
                    qr_gen_visitor_language,
                    qr_gen_visitor_visited_at,
                    qr_gen_visitor_duration_seconds
                FROM qr_gen_visitor
                ORDER BY qr_gen_visitor_id ASC
                """);

        int migratedCount = 0;
        for (Map<String, Object> legacyVisitor : legacyVisitors) {
            Optional<PackVisitorClassification> classification =
                    packVisitorRouteClassifier.classify((String) legacyVisitor.get("qr_gen_visitor_page_url"));

            if (classification.isEmpty()) {
                continue;
            }

            targetJdbcTemplate.update("""
                            INSERT INTO grap_pack_visitor (
                                session_id,
                                auth_scope,
                                auth_user_id,
                                service_code,
                                menu_code,
                                route_key,
                                page_url,
                                referrer,
                                ip_address,
                                user_agent,
                                browser_name,
                                browser_version,
                                os_name,
                                os_version,
                                device_type,
                                screen_resolution,
                                language,
                                visited_at,
                                duration_seconds,
                                visible_duration_seconds,
                                interaction_count,
                                first_interaction_at,
                                human_verified,
                                human_verified_at,
                                legacy_source,
                                legacy_source_id
                            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                            ON DUPLICATE KEY UPDATE
                                auth_scope = VALUES(auth_scope),
                                auth_user_id = VALUES(auth_user_id),
                                service_code = VALUES(service_code),
                                menu_code = VALUES(menu_code),
                                route_key = VALUES(route_key),
                                page_url = VALUES(page_url),
                                referrer = VALUES(referrer),
                                ip_address = VALUES(ip_address),
                                user_agent = VALUES(user_agent),
                                browser_name = VALUES(browser_name),
                                browser_version = VALUES(browser_version),
                                os_name = VALUES(os_name),
                                os_version = VALUES(os_version),
                                device_type = VALUES(device_type),
                                screen_resolution = VALUES(screen_resolution),
                                language = VALUES(language),
                                visited_at = VALUES(visited_at),
                                duration_seconds = VALUES(duration_seconds),
                                visible_duration_seconds = GREATEST(COALESCE(visible_duration_seconds, 0), VALUES(visible_duration_seconds)),
                                interaction_count = GREATEST(COALESCE(interaction_count, 0), VALUES(interaction_count))
                            """,
                    stringValue(legacyVisitor.get("qr_gen_visitor_session_id")),
                    resolveAuthScope(legacyVisitor.get("qr_gen_visitor_user_id")).name(),
                    longValue(legacyVisitor.get("qr_gen_visitor_user_id")),
                    classification.get().getServiceCode().name(),
                    classification.get().getMenuCode().name(),
                    classification.get().getRouteKey(),
                    stringValue(legacyVisitor.get("qr_gen_visitor_page_url")),
                    stringValue(legacyVisitor.get("qr_gen_visitor_referrer")),
                    stringValue(legacyVisitor.get("qr_gen_visitor_ip_address")),
                    stringValue(legacyVisitor.get("qr_gen_visitor_user_agent")),
                    stringValue(legacyVisitor.get("qr_gen_visitor_browser_name")),
                    stringValue(legacyVisitor.get("qr_gen_visitor_browser_version")),
                    stringValue(legacyVisitor.get("qr_gen_visitor_os_name")),
                    stringValue(legacyVisitor.get("qr_gen_visitor_os_version")),
                    stringValue(legacyVisitor.get("qr_gen_visitor_device_type")),
                    stringValue(legacyVisitor.get("qr_gen_visitor_screen_resolution")),
                    stringValue(legacyVisitor.get("qr_gen_visitor_language")),
                    timestampToLocalDateTime(legacyVisitor.get("qr_gen_visitor_visited_at")),
                    integerValue(legacyVisitor.get("qr_gen_visitor_duration_seconds")),
                    0,
                    0,
                    null,
                    false,
                    null,
                    LEGACY_SOURCE,
                    longValue(legacyVisitor.get("qr_gen_visitor_id"))
            );

            migratedCount++;
        }

        log.info("✅ [CHECK] QRgen 레거시 방문자 이관 완료: migratedCount={}", migratedCount);
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

    private void ensureColumn(JdbcTemplate jdbcTemplate, String columnName, String alterSql) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM information_schema.columns
                        WHERE table_schema = DATABASE()
                          AND table_name = 'grap_pack_visitor'
                          AND column_name = ?
                        """,
                Integer.class,
                columnName
        );

        if (count == null || count == 0) {
            jdbcTemplate.execute(alterSql);
            log.info("✅ [CHECK] 공통 방문자 컬럼 추가 완료: column={}", columnName);
        }
    }

    private void ensureIndex(JdbcTemplate jdbcTemplate, String indexName, String createIndexSql) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM information_schema.statistics
                        WHERE table_schema = DATABASE()
                          AND table_name = 'grap_pack_visitor'
                          AND index_name = ?
                        """,
                Integer.class,
                indexName
        );

        if (count == null || count == 0) {
            jdbcTemplate.execute(createIndexSql);
            log.info("✅ [CHECK] 공통 방문자 인덱스 추가 완료: index={}", indexName);
        }
    }

    private PackVisitorAuthScope resolveAuthScope(Object authUserId) {
        return longValue(authUserId) != null
                ? PackVisitorAuthScope.QRGEN_USER
                : PackVisitorAuthScope.ANONYMOUS;
    }

    private String stringValue(Object value) {
        return value != null ? value.toString() : null;
    }

    private Long longValue(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    private Integer integerValue(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        return null;
    }

    private LocalDateTime timestampToLocalDateTime(Object value) {
        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }
        return LocalDateTime.now();
    }
}
