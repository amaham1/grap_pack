-- ============================================
-- GRAP CMS Database Schema for MySQL
-- ============================================

-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS grap_cms
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE grap_cms;

-- ============================================
-- 1. 관리자 테이블 (admin)
-- ============================================
DROP TABLE IF EXISTS admin;

CREATE TABLE admin (
    admin_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '관리자 ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '사용자명 (로그인 ID)',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호 (암호화)',
    name VARCHAR(100) NOT NULL COMMENT '이름',
    create_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    create_name VARCHAR(100) COMMENT '생성자명',
    update_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    update_name VARCHAR(100) COMMENT '수정자명',
    delete_dt DATETIME COMMENT '삭제일시',
    delete_name VARCHAR(100) COMMENT '삭제자명',

    INDEX idx_username (username),
    INDEX idx_create_dt (create_dt),
    INDEX idx_delete_dt (delete_dt)
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
    create_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    create_name VARCHAR(100) COMMENT '생성자명',
    update_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    update_name VARCHAR(100) COMMENT '수정자명',
    delete_dt DATETIME COMMENT '삭제일시',
    delete_name VARCHAR(100) COMMENT '삭제자명',

    INDEX idx_type_name (type_name),
    INDEX idx_is_active (is_active),
    INDEX idx_create_dt (create_dt),
    INDEX idx_delete_dt (delete_dt)
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
    create_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    create_name VARCHAR(100) COMMENT '생성자명',
    update_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    update_name VARCHAR(100) COMMENT '수정자명',
    delete_dt DATETIME COMMENT '삭제일시',
    delete_name VARCHAR(100) COMMENT '삭제자명',

    INDEX idx_content_type_id (content_type_id),
    INDEX idx_is_published (is_published),
    INDEX idx_create_dt (create_dt),
    INDEX idx_view_count (view_count),
    INDEX idx_title (title),
    INDEX idx_delete_dt (delete_dt),

    CONSTRAINT fk_content_type
        FOREIGN KEY (content_type_id)
        REFERENCES content_type(content_type_id)
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
    create_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    create_name VARCHAR(100) COMMENT '생성자명',
    update_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    update_name VARCHAR(100) COMMENT '수정자명',
    delete_dt DATETIME COMMENT '삭제일시',
    delete_name VARCHAR(100) COMMENT '삭제자명',

    INDEX idx_content_id (content_id),
    INDEX idx_display_order (display_order),
    INDEX idx_create_dt (create_dt),
    INDEX idx_delete_dt (delete_dt),

    CONSTRAINT fk_image_content
        FOREIGN KEY (content_id)
        REFERENCES content(content_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='이미지';

-- ============================================
-- 5. 제주도 외부 공공 API 데이터 저장용 테이블
-- ============================================

-- 5-1. 축제/행사 테이블
CREATE TABLE IF NOT EXISTS festivals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 ID',
    original_api_id VARCHAR(100) UNIQUE NOT NULL COMMENT '외부 API 원본 ID (seq)',

    -- 기본 정보
    title VARCHAR(500) NOT NULL COMMENT '축제/행사명',
    content_html TEXT COMMENT 'HTML 형식 내용',
    source_url VARCHAR(1000) COMMENT '원본 페이지 URL',
    writer_name VARCHAR(200) COMMENT '작성자명',
    written_date DATETIME COMMENT '작성일',

    -- 첨부 파일 정보 (JSON)
    files_info JSON COMMENT '첨부 파일 정보 (JSON 배열)',

    -- 원본 API 데이터 보관
    api_raw_data JSON COMMENT 'API 원본 데이터 (JSON)',

    -- 관리 정보
    is_show BOOLEAN DEFAULT FALSE COMMENT '노출 여부',
    admin_memo TEXT COMMENT '관리자 메모',

    -- 검수 정보
    is_confirmed BOOLEAN DEFAULT FALSE COMMENT '검수 완료 여부',
    confirm_status VARCHAR(50) DEFAULT 'PENDING' COMMENT '검수 상태 (PENDING/APPROVED/REJECTED)',
    confirmed_by VARCHAR(100) COMMENT '검수자명',
    confirmed_at DATETIME COMMENT '검수일시',
    confirm_memo TEXT COMMENT '검수 메모',

    -- 시스템 정보
    fetched_at DATETIME NOT NULL COMMENT 'API 데이터 수집 시간',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',

    INDEX idx_original_api_id (original_api_id),
    INDEX idx_is_show (is_show),
    INDEX idx_is_confirmed (is_confirmed),
    INDEX idx_confirm_status (confirm_status),
    INDEX idx_written_date (written_date),
    INDEX idx_fetched_at (fetched_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='제주도 축제/행사 정보 (외부 API)';

-- 5-2. 공연/전시 테이블
CREATE TABLE IF NOT EXISTS exhibitions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 ID',
    original_api_id VARCHAR(100) UNIQUE NOT NULL COMMENT '외부 API 원본 ID (seq)',

    -- 기본 정보
    title VARCHAR(500) NOT NULL COMMENT '공연/전시명',
    category_name VARCHAR(100) COMMENT '카테고리명 (전시, 공연, 행사 등)',
    cover_image_url VARCHAR(1000) COMMENT '커버 이미지 URL',

    -- 일정 정보
    start_date DATETIME COMMENT '시작일',
    end_date DATETIME COMMENT '종료일',
    time_info VARCHAR(500) COMMENT '운영 시간 정보',

    -- 장소 및 주최 정보
    location_name VARCHAR(500) COMMENT '장소명',
    organizer_info VARCHAR(500) COMMENT '주최/주관 정보',
    tel_number VARCHAR(100) COMMENT '문의 전화번호',

    -- 요금 및 상태
    pay_info VARCHAR(500) COMMENT '요금 정보',
    status_info VARCHAR(200) COMMENT '상태 정보',
    division_name VARCHAR(200) COMMENT '구분명',

    -- 원본 API 데이터 보관
    api_raw_data JSON COMMENT 'API 원본 데이터 (JSON)',

    -- 등록 정보
    source_type VARCHAR(20) DEFAULT 'API' COMMENT '등록 유형 (API: 외부API, USER: 사용자요청)',
    writer_name VARCHAR(100) COMMENT '작성자명 (사용자 등록 요청 시)',

    -- 관리 정보
    is_show BOOLEAN DEFAULT FALSE COMMENT '노출 여부',
    admin_memo TEXT COMMENT '관리자 메모',

    -- 검수 정보
    is_confirmed BOOLEAN DEFAULT FALSE COMMENT '검수 완료 여부',
    confirm_status VARCHAR(50) DEFAULT 'PENDING' COMMENT '검수 상태 (PENDING/APPROVED/REJECTED)',
    confirmed_by VARCHAR(100) COMMENT '검수자명',
    confirmed_at DATETIME COMMENT '검수일시',
    confirm_memo TEXT COMMENT '검수 메모',

    -- 시스템 정보
    fetched_at DATETIME NOT NULL COMMENT 'API 데이터 수집 시간',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',

    INDEX idx_original_api_id (original_api_id),
    INDEX idx_is_show (is_show),
    INDEX idx_is_confirmed (is_confirmed),
    INDEX idx_confirm_status (confirm_status),
    INDEX idx_category_name (category_name),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date),
    INDEX idx_fetched_at (fetched_at),
    INDEX idx_source_type (source_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='제주도 공연/전시 정보';

-- 5-3. 복지 서비스 테이블
CREATE TABLE IF NOT EXISTS welfare_services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 ID',
    original_api_id VARCHAR(100) UNIQUE NOT NULL COMMENT '외부 API 원본 ID (seq)',

    -- 기본 정보
    service_name VARCHAR(500) NOT NULL COMMENT '복지 서비스명',

    -- 지역 정보
    is_all_location BOOLEAN DEFAULT FALSE COMMENT '전체 지역 해당 여부',
    is_jeju_location BOOLEAN DEFAULT FALSE COMMENT '제주시 해당 여부',
    is_seogwipo_location BOOLEAN DEFAULT FALSE COMMENT '서귀포시 해당 여부',

    -- 서비스 내용 (HTML)
    support_target_html TEXT COMMENT '지원 대상 정보 (HTML)',
    support_content_html TEXT COMMENT '지원 내용 정보 (HTML)',
    application_info_html TEXT COMMENT '신청 방법 정보 (HTML)',

    -- 원본 API 데이터 보관
    api_raw_data JSON COMMENT 'API 원본 데이터 (JSON)',

    -- 관리 정보
    is_show BOOLEAN DEFAULT FALSE COMMENT '노출 여부',
    admin_memo TEXT COMMENT '관리자 메모',

    -- 검수 정보
    is_confirmed BOOLEAN DEFAULT FALSE COMMENT '검수 완료 여부',
    confirm_status VARCHAR(50) DEFAULT 'PENDING' COMMENT '검수 상태 (PENDING/APPROVED/REJECTED)',
    confirmed_by VARCHAR(100) COMMENT '검수자명',
    confirmed_at DATETIME COMMENT '검수일시',
    confirm_memo TEXT COMMENT '검수 메모',

    -- 시스템 정보
    fetched_at DATETIME NOT NULL COMMENT 'API 데이터 수집 시간',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',

    INDEX idx_original_api_id (original_api_id),
    INDEX idx_is_show (is_show),
    INDEX idx_is_confirmed (is_confirmed),
    INDEX idx_confirm_status (confirm_status),
    INDEX idx_is_all_location (is_all_location),
    INDEX idx_is_jeju_location (is_jeju_location),
    INDEX idx_is_seogwipo_location (is_seogwipo_location),
    INDEX idx_fetched_at (fetched_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='제주도 복지 서비스 정보 (외부 API)';

-- 5-4. 주유소 기본 정보 테이블
CREATE TABLE IF NOT EXISTS gas_stations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 ID',
    opinet_id VARCHAR(50) UNIQUE NOT NULL COMMENT 'Opinet 주유소 고유 ID',

    -- 주유소 정보
    station_name VARCHAR(200) NOT NULL COMMENT '주유소명',
    brand VARCHAR(100) COMMENT '브랜드명 (SK, GS, S-OIL 등)',

    -- 위치 정보
    address VARCHAR(500) COMMENT '주소',
    latitude DECIMAL(10, 7) COMMENT '위도',
    longitude DECIMAL(10, 7) COMMENT '경도',

    -- 연락처
    phone VARCHAR(50) COMMENT '전화번호',

    -- 원본 API 데이터 보관
    api_raw_data JSON COMMENT 'API 원본 데이터 (JSON)',

    -- 시스템 정보
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',

    INDEX idx_opinet_id (opinet_id),
    INDEX idx_station_name (station_name),
    INDEX idx_location (latitude, longitude)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='제주도 주유소 기본 정보 (외부 API)';

-- 5-5. 주유소 가격 정보 테이블
CREATE TABLE IF NOT EXISTS gas_prices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 ID',
    opinet_id VARCHAR(50) NOT NULL COMMENT 'Opinet 주유소 고유 ID',

    -- 가격 정보 (단위: 원)
    gasoline_price INT DEFAULT 0 COMMENT '휘발유 가격',
    premium_gasoline_price INT DEFAULT 0 COMMENT '고급 휘발유 가격',
    diesel_price INT DEFAULT 0 COMMENT '경유 가격',
    lpg_price INT DEFAULT 0 COMMENT 'LPG 가격',

    -- 날짜 정보
    price_date DATE NOT NULL COMMENT '가격 기준일',

    -- 원본 API 데이터 보관
    api_raw_data JSON COMMENT 'API 원본 데이터 (JSON)',

    -- 시스템 정보
    fetched_at DATETIME NOT NULL COMMENT 'API 데이터 수집 시간',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',

    UNIQUE KEY unique_price_per_date (opinet_id, price_date),
    INDEX idx_opinet_id (opinet_id),
    INDEX idx_price_date (price_date),
    INDEX idx_fetched_at (fetched_at),

    CONSTRAINT fk_gas_prices_station
        FOREIGN KEY (opinet_id)
        REFERENCES gas_stations(opinet_id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='제주도 주유소 가격 정보 (외부 API)';

-- ============================================
-- 6. 사용자 등록 요청 이미지 테이블
-- ============================================

-- 6-1. 축제/행사 이미지 테이블
CREATE TABLE IF NOT EXISTS festivals_image (
    image_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '이미지 ID',
    festival_id BIGINT NOT NULL COMMENT '축제/행사 ID (festivals.id FK)',
    original_name VARCHAR(255) NOT NULL COMMENT '원본 파일명',
    saved_name VARCHAR(255) NOT NULL COMMENT '저장된 파일명',
    file_path VARCHAR(500) NOT NULL COMMENT '파일 경로',
    file_size BIGINT NOT NULL COMMENT '파일 크기 (bytes)',
    is_thumbnail BOOLEAN NOT NULL DEFAULT FALSE COMMENT '썸네일 여부',
    display_order INT NOT NULL DEFAULT 0 COMMENT '표시 순서',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',

    INDEX idx_festival_id (festival_id),
    INDEX idx_thumbnail (festival_id, is_thumbnail),

    CONSTRAINT fk_festivals_image FOREIGN KEY (festival_id)
        REFERENCES festivals(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='축제/행사 이미지';

-- 6-2. 공연/전시 이미지 테이블
CREATE TABLE IF NOT EXISTS exhibitions_image (
    image_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '이미지 ID',
    exhibition_id BIGINT NOT NULL COMMENT '공연/전시 ID (exhibitions.id FK)',
    original_name VARCHAR(255) NOT NULL COMMENT '원본 파일명',
    saved_name VARCHAR(255) NOT NULL COMMENT '저장된 파일명',
    file_path VARCHAR(500) NOT NULL COMMENT '파일 경로',
    file_size BIGINT NOT NULL COMMENT '파일 크기 (bytes)',
    is_thumbnail BOOLEAN NOT NULL DEFAULT FALSE COMMENT '썸네일 여부',
    display_order INT NOT NULL DEFAULT 0 COMMENT '표시 순서',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',

    INDEX idx_exhibition_id (exhibition_id),
    INDEX idx_thumbnail (exhibition_id, is_thumbnail),

    CONSTRAINT fk_exhibitions_image FOREIGN KEY (exhibition_id)
        REFERENCES exhibitions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='공연/전시 이미지';

-- 6-3. 복지서비스 이미지 테이블
CREATE TABLE IF NOT EXISTS welfare_services_image (
    image_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '이미지 ID',
    welfare_service_id BIGINT NOT NULL COMMENT '복지서비스 ID (welfare_services.id FK)',
    original_name VARCHAR(255) NOT NULL COMMENT '원본 파일명',
    saved_name VARCHAR(255) NOT NULL COMMENT '저장된 파일명',
    file_path VARCHAR(500) NOT NULL COMMENT '파일 경로',
    file_size BIGINT NOT NULL COMMENT '파일 크기 (bytes)',
    is_thumbnail BOOLEAN NOT NULL DEFAULT FALSE COMMENT '썸네일 여부',
    display_order INT NOT NULL DEFAULT 0 COMMENT '표시 순서',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',

    INDEX idx_welfare_service_id (welfare_service_id),
    INDEX idx_thumbnail (welfare_service_id, is_thumbnail),

    CONSTRAINT fk_welfare_services_image FOREIGN KEY (welfare_service_id)
        REFERENCES welfare_services(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='복지서비스 이미지';
