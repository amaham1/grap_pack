-- ============================================
-- QR Generator 서비스 스키마
-- ============================================

-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS qr_gen
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE qr_gen;

-- 사용자 테이블
CREATE TABLE IF NOT EXISTS qr_gen_user (
    qr_gen_user_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 ID',
    qr_gen_user_username VARCHAR(50) NOT NULL UNIQUE COMMENT '로그인 ID',
    qr_gen_user_password VARCHAR(255) NOT NULL COMMENT '비밀번호 (BCrypt)',
    qr_gen_user_email VARCHAR(200) COMMENT '이메일',
    qr_gen_user_nickname VARCHAR(100) COMMENT '닉네임',
    qr_gen_user_is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성화 여부',
    qr_gen_user_created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    qr_gen_user_updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    qr_gen_user_last_login_at DATETIME COMMENT '마지막 로그인 일시',

    INDEX idx_qr_gen_user_username (qr_gen_user_username),
    INDEX idx_qr_gen_user_email (qr_gen_user_email),
    INDEX idx_qr_gen_user_is_active (qr_gen_user_is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='QR Generator 사용자';

-- QR 생성 이력 테이블
CREATE TABLE IF NOT EXISTS qr_gen_history (
    qr_gen_history_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '이력 ID',
    qr_gen_history_user_id BIGINT NOT NULL COMMENT '사용자 ID (FK)',

    -- QR 코드 정보
    qr_gen_history_content_type VARCHAR(50) NOT NULL COMMENT 'QR 콘텐츠 타입 (URL, TEXT, VCARD, WIFI, EMAIL, SMS, PHONE, GEO)',
    qr_gen_history_content_value TEXT NOT NULL COMMENT 'QR 코드에 인코딩된 값',

    -- 생성 옵션
    qr_gen_history_size INT NOT NULL DEFAULT 300 COMMENT 'QR 코드 크기 (px)',
    qr_gen_history_error_correction VARCHAR(10) NOT NULL DEFAULT 'M' COMMENT '에러 보정 레벨 (L, M, Q, H)',
    qr_gen_history_fg_color VARCHAR(7) DEFAULT '#000000' COMMENT '전경색 (HEX)',
    qr_gen_history_bg_color VARCHAR(7) DEFAULT '#FFFFFF' COMMENT '배경색 (HEX)',

    -- 파일 정보
    qr_gen_history_image_path VARCHAR(500) COMMENT 'QR 이미지 상대경로',

    -- 메타 정보
    qr_gen_history_title VARCHAR(200) COMMENT '사용자 지정 제목',
    qr_gen_history_memo TEXT COMMENT '메모',

    -- 시스템 정보
    qr_gen_history_created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',

    INDEX idx_qr_gen_history_user_id (qr_gen_history_user_id),
    INDEX idx_qr_gen_history_content_type (qr_gen_history_content_type),
    INDEX idx_qr_gen_history_created_at (qr_gen_history_created_at),

    CONSTRAINT fk_qr_gen_history_user
        FOREIGN KEY (qr_gen_history_user_id) REFERENCES qr_gen_user(qr_gen_user_id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='QR Generator 생성 이력';
