-- ============================================
-- 검수 기능 추가를 위한 컬럼 추가 마이그레이션
-- ============================================

USE grap_cms;

-- 1. festivals 테이블에 검수 컬럼 추가
ALTER TABLE festivals
    ADD COLUMN is_confirmed BOOLEAN DEFAULT FALSE COMMENT '검수 완료 여부' AFTER admin_memo,
    ADD COLUMN confirm_status VARCHAR(50) DEFAULT 'PENDING' COMMENT '검수 상태 (PENDING/APPROVED/REJECTED)' AFTER is_confirmed,
    ADD COLUMN confirmed_by VARCHAR(100) COMMENT '검수자명' AFTER confirm_status,
    ADD COLUMN confirmed_at DATETIME COMMENT '검수일시' AFTER confirmed_by,
    ADD COLUMN confirm_memo TEXT COMMENT '검수 메모' AFTER confirmed_at,
    ADD INDEX idx_is_confirmed (is_confirmed),
    ADD INDEX idx_confirm_status (confirm_status);

-- 2. exhibitions 테이블에 검수 컬럼 추가
ALTER TABLE exhibitions
    ADD COLUMN is_confirmed BOOLEAN DEFAULT FALSE COMMENT '검수 완료 여부' AFTER admin_memo,
    ADD COLUMN confirm_status VARCHAR(50) DEFAULT 'PENDING' COMMENT '검수 상태 (PENDING/APPROVED/REJECTED)' AFTER is_confirmed,
    ADD COLUMN confirmed_by VARCHAR(100) COMMENT '검수자명' AFTER confirm_status,
    ADD COLUMN confirmed_at DATETIME COMMENT '검수일시' AFTER confirmed_by,
    ADD COLUMN confirm_memo TEXT COMMENT '검수 메모' AFTER confirmed_at,
    ADD INDEX idx_is_confirmed (is_confirmed),
    ADD INDEX idx_confirm_status (confirm_status);

-- 3. welfare_services 테이블에 검수 컬럼 추가
ALTER TABLE welfare_services
    ADD COLUMN is_confirmed BOOLEAN DEFAULT FALSE COMMENT '검수 완료 여부' AFTER admin_memo,
    ADD COLUMN confirm_status VARCHAR(50) DEFAULT 'PENDING' COMMENT '검수 상태 (PENDING/APPROVED/REJECTED)' AFTER is_confirmed,
    ADD COLUMN confirmed_by VARCHAR(100) COMMENT '검수자명' AFTER confirm_status,
    ADD COLUMN confirmed_at DATETIME COMMENT '검수일시' AFTER confirmed_by,
    ADD COLUMN confirm_memo TEXT COMMENT '검수 메모' AFTER confirmed_at,
    ADD INDEX idx_is_confirmed (is_confirmed),
    ADD INDEX idx_confirm_status (confirm_status);
