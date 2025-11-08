-- ============================================
-- CMS Database Schema for MySQL/MariaDB
-- ============================================

-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS cms
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE cms;

-- ============================================
-- 1. 관리자 테이블 (admin)
-- ============================================
DROP TABLE IF EXISTS admin;

CREATE TABLE admin (
    admin_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '관리자 ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '사용자명 (로그인 ID)',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호 (암호화)',
    name VARCHAR(100) NOT NULL COMMENT '이름',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',

    INDEX idx_username (username),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='관리자';

-- ============================================
-- 2. 콘텐츠 타입 테이블 (content_type)
-- ============================================
DROP TABLE IF EXISTS content_type;

CREATE TABLE content_type (
    content_type_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '콘텐츠 타입 ID',
    type_name VARCHAR(100) NOT NULL UNIQUE COMMENT '타입명',
    type_desc VARCHAR(500) COMMENT '타입 설명',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성화 여부',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',

    INDEX idx_type_name (type_name),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='콘텐츠 타입';

-- ============================================
-- 3. 콘텐츠 테이블 (content)
-- ============================================
DROP TABLE IF EXISTS content;

CREATE TABLE content (
    content_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '콘텐츠 ID',
    content_type_id BIGINT NOT NULL COMMENT '콘텐츠 타입 ID',
    title VARCHAR(200) NOT NULL COMMENT '제목',
    content TEXT NOT NULL COMMENT '내용',
    is_published BOOLEAN NOT NULL DEFAULT FALSE COMMENT '공개 여부',
    view_count INT NOT NULL DEFAULT 0 COMMENT '조회수',
    created_by BIGINT NOT NULL COMMENT '작성자 ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',

    INDEX idx_content_type_id (content_type_id),
    INDEX idx_created_by (created_by),
    INDEX idx_is_published (is_published),
    INDEX idx_created_at (created_at),
    INDEX idx_view_count (view_count),
    INDEX idx_title (title),

    CONSTRAINT fk_content_type
        FOREIGN KEY (content_type_id)
        REFERENCES content_type(content_type_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT fk_content_admin
        FOREIGN KEY (created_by)
        REFERENCES admin(admin_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='콘텐츠';

-- ============================================
-- 4. 이미지 테이블 (image)
-- ============================================
DROP TABLE IF EXISTS image;

CREATE TABLE image (
    image_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '이미지 ID',
    content_id BIGINT NOT NULL COMMENT '콘텐츠 ID',
    original_name VARCHAR(255) NOT NULL COMMENT '원본 파일명',
    saved_name VARCHAR(255) NOT NULL COMMENT '저장된 파일명',
    file_path VARCHAR(500) NOT NULL COMMENT '파일 경로',
    file_size BIGINT NOT NULL COMMENT '파일 크기 (bytes)',
    display_order INT NOT NULL DEFAULT 0 COMMENT '표시 순서',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',

    INDEX idx_content_id (content_id),
    INDEX idx_display_order (display_order),
    INDEX idx_created_at (created_at),

    CONSTRAINT fk_image_content
        FOREIGN KEY (content_id)
        REFERENCES content(content_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='이미지';
