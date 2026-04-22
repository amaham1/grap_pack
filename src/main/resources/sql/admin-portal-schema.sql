-- ============================================
-- Admin Portal Schema
-- ============================================

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='통합 운영 포털 운영자';

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='통합 운영 포털 액션 로그';
