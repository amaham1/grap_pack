-- =====================================================
-- QR 관리 서비스 데이터베이스 스키마
-- =====================================================

-- 최고관리자 테이블
CREATE TABLE IF NOT EXISTS qr_manage_super_admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '최고관리자 ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '사용자명 (로그인 ID)',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호 (BCrypt 암호화)',
    name VARCHAR(100) NOT NULL COMMENT '이름',
    email VARCHAR(100) COMMENT '이메일',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시'
) COMMENT='QR 관리 서비스 최고관리자';

-- 상점관리자 테이블
CREATE TABLE IF NOT EXISTS qr_manage_shop_admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '상점관리자 ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '사용자명 (로그인 ID)',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호 (BCrypt 암호화)',
    name VARCHAR(100) NOT NULL COMMENT '이름',
    email VARCHAR(100) NOT NULL COMMENT '이메일',
    phone VARCHAR(20) COMMENT '연락처',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '계정 상태 (PENDING, ACTIVE, INACTIVE, SUSPENDED)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    INDEX idx_status (status),
    INDEX idx_email (email)
) COMMENT='QR 관리 서비스 상점관리자';

-- 비밀번호 재설정 토큰 테이블
CREATE TABLE IF NOT EXISTS qr_manage_password_reset_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '토큰 ID',
    shop_admin_id BIGINT NOT NULL COMMENT '상점관리자 ID',
    token VARCHAR(100) NOT NULL UNIQUE COMMENT '토큰 문자열 (UUID)',
    expires_at DATETIME NOT NULL COMMENT '만료일시',
    used BOOLEAN DEFAULT FALSE COMMENT '사용 여부',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    FOREIGN KEY (shop_admin_id) REFERENCES qr_manage_shop_admin(id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_shop_admin_id (shop_admin_id)
) COMMENT='비밀번호 재설정 토큰';

-- 상점 테이블
CREATE TABLE IF NOT EXISTS qr_manage_shop (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '상점 ID',
    shop_admin_id BIGINT NOT NULL COMMENT '상점관리자 ID',
    name VARCHAR(100) NOT NULL COMMENT '상점명',
    description TEXT COMMENT '상점 설명',
    address VARCHAR(255) COMMENT '주소',
    phone VARCHAR(20) COMMENT '연락처',
    is_visible BOOLEAN DEFAULT FALSE COMMENT '노출 여부 (최고관리자 관리)',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '상태 (PENDING, APPROVED, REJECTED)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    FOREIGN KEY (shop_admin_id) REFERENCES qr_manage_shop_admin(id),
    INDEX idx_shop_admin_id (shop_admin_id),
    INDEX idx_status (status),
    INDEX idx_is_visible (is_visible)
) COMMENT='상점';

-- 영업시간 테이블
CREATE TABLE IF NOT EXISTS qr_manage_business_hours (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '영업시간 ID',
    shop_id BIGINT NOT NULL COMMENT '상점 ID',
    day_of_week TINYINT NOT NULL COMMENT '요일 (0=일, 1=월, ..., 6=토)',
    is_holiday BOOLEAN DEFAULT FALSE COMMENT '휴무일 여부',
    open_time TIME COMMENT '오픈 시간',
    close_time TIME COMMENT '마감 시간',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    FOREIGN KEY (shop_id) REFERENCES qr_manage_shop(id) ON DELETE CASCADE,
    UNIQUE KEY uk_shop_day (shop_id, day_of_week)
) COMMENT='상점 영업시간';

-- 상점 검수 이력 테이블
CREATE TABLE IF NOT EXISTS qr_manage_shop_review_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '검수 이력 ID',
    shop_id BIGINT NOT NULL COMMENT '상점 ID',
    reviewer_id BIGINT NOT NULL COMMENT '검수자 (최고관리자) ID',
    action VARCHAR(20) NOT NULL COMMENT '처리 (APPROVED, REJECTED)',
    comment TEXT COMMENT '코멘트',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    FOREIGN KEY (shop_id) REFERENCES qr_manage_shop(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewer_id) REFERENCES qr_manage_super_admin(id),
    INDEX idx_shop_id (shop_id)
) COMMENT='상점 검수 이력';

-- 상점 메모 테이블
CREATE TABLE IF NOT EXISTS qr_manage_shop_memo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '메모 ID',
    shop_id BIGINT NOT NULL COMMENT '상점 ID',
    admin_id BIGINT NOT NULL COMMENT '작성자 (최고관리자) ID',
    content TEXT NOT NULL COMMENT '메모 내용',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    FOREIGN KEY (shop_id) REFERENCES qr_manage_shop(id) ON DELETE CASCADE,
    FOREIGN KEY (admin_id) REFERENCES qr_manage_super_admin(id),
    INDEX idx_shop_id (shop_id)
) COMMENT='상점 메모 (최고관리자용)';

-- 카테고리 테이블
CREATE TABLE IF NOT EXISTS qr_manage_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '카테고리 ID',
    shop_id BIGINT NOT NULL COMMENT '상점 ID',
    name VARCHAR(100) NOT NULL COMMENT '카테고리명',
    description TEXT COMMENT '카테고리 설명',
    sort_order INT DEFAULT 0 COMMENT '정렬 순서',
    is_visible BOOLEAN DEFAULT TRUE COMMENT '공개 여부',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    FOREIGN KEY (shop_id) REFERENCES qr_manage_shop(id) ON DELETE CASCADE,
    INDEX idx_shop_id (shop_id)
) COMMENT='카테고리';

-- 메뉴 이미지 테이블
CREATE TABLE IF NOT EXISTS qr_manage_menu_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '이미지 ID',
    menu_id BIGINT NOT NULL COMMENT '메뉴 ID',
    file_name VARCHAR(255) NOT NULL COMMENT '파일명',
    file_path VARCHAR(500) NOT NULL COMMENT '파일 경로',
    file_size INT COMMENT '파일 크기 (bytes)',
    sort_order INT DEFAULT 0 COMMENT '정렬 순서',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    INDEX idx_menu_id (menu_id)
) COMMENT='메뉴 이미지';

-- 메뉴 테이블
CREATE TABLE IF NOT EXISTS qr_manage_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '메뉴 ID',
    shop_id BIGINT NOT NULL COMMENT '상점 ID',
    category_id BIGINT COMMENT '카테고리 ID',
    name VARCHAR(100) NOT NULL COMMENT '메뉴명',
    description TEXT COMMENT '메뉴 설명',
    price INT NOT NULL COMMENT '가격',
    primary_image_id BIGINT COMMENT '대표 이미지 ID',
    is_visible BOOLEAN DEFAULT TRUE COMMENT '공개 여부',
    is_sold_out BOOLEAN DEFAULT FALSE COMMENT '품절 여부',
    sort_order INT DEFAULT 0 COMMENT '정렬 순서',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    FOREIGN KEY (shop_id) REFERENCES qr_manage_shop(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES qr_manage_category(id) ON DELETE SET NULL,
    INDEX idx_shop_id (shop_id),
    INDEX idx_category_id (category_id)
) COMMENT='메뉴';

-- 메뉴 옵션 그룹 테이블
CREATE TABLE IF NOT EXISTS qr_manage_menu_option_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '옵션 그룹 ID',
    menu_id BIGINT NOT NULL COMMENT '메뉴 ID',
    name VARCHAR(100) NOT NULL COMMENT '옵션 그룹명 (예: 사이즈 선택)',
    is_required BOOLEAN DEFAULT FALSE COMMENT '필수 선택 여부',
    sort_order INT DEFAULT 0 COMMENT '정렬 순서',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    FOREIGN KEY (menu_id) REFERENCES qr_manage_menu(id) ON DELETE CASCADE,
    INDEX idx_menu_id (menu_id)
) COMMENT='메뉴 옵션 그룹';

-- 메뉴 옵션 테이블
CREATE TABLE IF NOT EXISTS qr_manage_menu_option (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '옵션 ID',
    option_group_id BIGINT NOT NULL COMMENT '옵션 그룹 ID',
    name VARCHAR(100) NOT NULL COMMENT '옵션명 (예: Small, Medium, Large)',
    sort_order INT DEFAULT 0 COMMENT '정렬 순서',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    FOREIGN KEY (option_group_id) REFERENCES qr_manage_menu_option_group(id) ON DELETE CASCADE,
    INDEX idx_option_group_id (option_group_id)
) COMMENT='메뉴 옵션';

-- QR 코드 테이블
CREATE TABLE IF NOT EXISTS qr_manage_qr_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'QR 코드 ID',
    shop_id BIGINT NOT NULL COMMENT '상점 ID',
    qr_type VARCHAR(20) NOT NULL COMMENT 'QR 유형 (SHOP, MENU)',
    qr_code VARCHAR(100) NOT NULL UNIQUE COMMENT 'QR 식별 코드 (UUID)',
    image_path VARCHAR(500) COMMENT 'QR 이미지 파일 경로',
    target_url VARCHAR(500) COMMENT 'QR 스캔 시 이동할 URL',
    expires_at DATETIME COMMENT '만료일시 (NULL이면 무제한)',
    is_active BOOLEAN DEFAULT TRUE COMMENT '활성화 여부',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    FOREIGN KEY (shop_id) REFERENCES qr_manage_shop(id) ON DELETE CASCADE,
    INDEX idx_shop_id (shop_id),
    INDEX idx_qr_code (qr_code),
    INDEX idx_qr_type (qr_type)
) COMMENT='QR 코드';

-- QR 스캔 로그 테이블
CREATE TABLE IF NOT EXISTS qr_manage_qr_scan_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '스캔 로그 ID',
    qr_code_id BIGINT NOT NULL COMMENT 'QR 코드 ID',
    scanned_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '스캔 일시',
    ip_address VARCHAR(50) COMMENT 'IP 주소',
    user_agent TEXT COMMENT 'User Agent',
    FOREIGN KEY (qr_code_id) REFERENCES qr_manage_qr_code(id) ON DELETE CASCADE,
    INDEX idx_qr_code_id (qr_code_id),
    INDEX idx_scanned_at (scanned_at)
) COMMENT='QR 스캔 로그';

-- 이미지 테이블
CREATE TABLE IF NOT EXISTS qr_manage_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '이미지 ID',
    target_type VARCHAR(20) NOT NULL COMMENT '대상 타입 (SHOP, MENU)',
    target_id BIGINT NOT NULL COMMENT '대상 ID',
    file_name VARCHAR(255) NOT NULL COMMENT '파일명',
    file_path VARCHAR(500) NOT NULL COMMENT '파일 경로',
    file_size INT COMMENT '파일 크기 (bytes)',
    is_primary BOOLEAN DEFAULT FALSE COMMENT '대표 이미지 여부',
    sort_order INT DEFAULT 0 COMMENT '정렬 순서',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    INDEX idx_target (target_type, target_id)
) COMMENT='이미지';

-- 알림 테이블
CREATE TABLE IF NOT EXISTS qr_manage_notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '알림 ID',
    recipient_type VARCHAR(20) NOT NULL COMMENT '수신자 타입 (SUPER_ADMIN, SHOP_ADMIN)',
    recipient_id BIGINT COMMENT '수신자 ID (상점관리자 ID, 최고관리자는 NULL)',
    notification_type VARCHAR(50) NOT NULL COMMENT '알림 유형',
    title VARCHAR(200) NOT NULL COMMENT '알림 제목',
    message TEXT COMMENT '알림 내용',
    link_url VARCHAR(500) COMMENT '관련 링크',
    is_read BOOLEAN DEFAULT FALSE COMMENT '읽음 여부',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    read_at DATETIME COMMENT '읽은 일시',
    INDEX idx_recipient (recipient_type, recipient_id),
    INDEX idx_is_read (is_read)
) COMMENT='알림';

-- 활동 로그 테이블
CREATE TABLE IF NOT EXISTS qr_manage_activity_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '활동 로그 ID',
    user_type VARCHAR(20) NOT NULL COMMENT '사용자 타입 (SUPER_ADMIN, SHOP_ADMIN)',
    user_id BIGINT COMMENT '사용자 ID',
    user_email VARCHAR(100) COMMENT '사용자 이메일',
    activity_type VARCHAR(50) NOT NULL COMMENT '활동 유형 (LOGIN, CREATE, UPDATE, DELETE, APPROVE, REJECT)',
    target_type VARCHAR(50) COMMENT '대상 타입 (SHOP, MENU, QR_CODE 등)',
    target_id BIGINT COMMENT '대상 ID',
    description TEXT COMMENT '활동 설명',
    ip_address VARCHAR(50) COMMENT 'IP 주소',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    INDEX idx_user (user_type, user_id),
    INDEX idx_activity_type (activity_type),
    INDEX idx_created_at (created_at)
) COMMENT='활동 로그';

-- =====================================================
-- 초기 데이터
-- =====================================================

-- 최고관리자 기본 계정 (비밀번호: admin123)
INSERT INTO qr_manage_super_admin (username, password, name, email)
VALUES ('superadmin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '최고관리자', 'super@qrmanage.com')
ON DUPLICATE KEY UPDATE username = username;
