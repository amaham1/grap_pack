-- 외부 API 테이블들을 최신 스키마로 재생성
-- 경고: 기존 데이터가 모두 삭제됩니다!

-- 1. 기존 테이블 삭제
DROP TABLE IF EXISTS gas_station_prices;
DROP TABLE IF EXISTS gas_stations;
DROP TABLE IF EXISTS welfare_services;
DROP TABLE IF EXISTS exhibitions;
DROP TABLE IF EXISTS festivals;

-- 2. 축제/행사 테이블
CREATE TABLE festivals (
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

    -- 시스템 정보
    fetched_at DATETIME NOT NULL COMMENT 'API 데이터 수집 시간',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',

    INDEX idx_original_api_id (original_api_id),
    INDEX idx_is_show (is_show),
    INDEX idx_written_date (written_date),
    INDEX idx_fetched_at (fetched_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='제주도 축제/행사 정보 (외부 API)';

-- 3. 공연/전시 테이블
CREATE TABLE exhibitions (
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

    -- 관리 정보
    is_show BOOLEAN DEFAULT FALSE COMMENT '노출 여부',
    admin_memo TEXT COMMENT '관리자 메모',

    -- 시스템 정보
    fetched_at DATETIME NOT NULL COMMENT 'API 데이터 수집 시간',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',

    INDEX idx_original_api_id (original_api_id),
    INDEX idx_is_show (is_show),
    INDEX idx_category_name (category_name),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date),
    INDEX idx_fetched_at (fetched_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='제주도 공연/전시 정보 (외부 API)';

-- 4. 복지 서비스 테이블
CREATE TABLE welfare_services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 ID',
    original_api_id VARCHAR(100) UNIQUE NOT NULL COMMENT '외부 API 원본 ID (seq)',

    -- 기본 정보
    service_name VARCHAR(500) NOT NULL COMMENT '복지 서비스명',

    -- 지역 정보
    is_all_location BOOLEAN DEFAULT FALSE COMMENT '전체 지역 해당 여부',
    is_jeju_location BOOLEAN DEFAULT FALSE COMMENT '제주시 해당 여부',
    is_seogwipo_location BOOLEAN DEFAULT FALSE COMMENT '서귀포시 해당 여부',

    -- 서비스 내용 (HTML)
    support_target_html TEXT COMMENT '지원 대상 (HTML)',
    support_content_html TEXT COMMENT '지원 내용 (HTML)',
    application_info_html TEXT COMMENT '신청 방법 (HTML)',

    -- 원본 API 데이터 보관
    api_raw_data JSON COMMENT 'API 원본 데이터 (JSON)',

    -- 관리 정보
    is_show BOOLEAN DEFAULT FALSE COMMENT '노출 여부',
    admin_memo TEXT COMMENT '관리자 메모',

    -- 시스템 정보
    fetched_at DATETIME NOT NULL COMMENT 'API 데이터 수집 시간',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',

    INDEX idx_original_api_id (original_api_id),
    INDEX idx_is_show (is_show),
    INDEX idx_location (is_all_location, is_jeju_location, is_seogwipo_location),
    INDEX idx_fetched_at (fetched_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='제주도 복지 서비스 정보 (외부 API)';

-- 5. 주유소 테이블
CREATE TABLE gas_stations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 ID',
    opinet_id VARCHAR(100) UNIQUE NOT NULL COMMENT 'Opinet 주유소 ID',

    -- 주유소 정보
    station_name VARCHAR(200) NOT NULL COMMENT '주유소명',
    brand VARCHAR(100) COMMENT '브랜드명',
    address VARCHAR(500) COMMENT '주소',
    phone VARCHAR(50) COMMENT '전화번호',

    -- 위치 정보
    latitude DECIMAL(10, 8) COMMENT '위도',
    longitude DECIMAL(11, 8) COMMENT '경도',

    -- 원본 API 데이터 보관
    api_raw_data JSON COMMENT 'API 원본 데이터 (JSON)',

    -- 시스템 정보
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',

    INDEX idx_opinet_id (opinet_id),
    INDEX idx_brand (brand),
    INDEX idx_location (latitude, longitude)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='주유소 정보 (Opinet API)';

-- 6. 주유소 가격 정보 테이블
CREATE TABLE gas_station_prices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 ID',
    station_id BIGINT NOT NULL COMMENT '주유소 ID (FK)',
    price_date DATE NOT NULL COMMENT '가격 기준일',

    -- 유종별 가격
    gasoline_price INT COMMENT '휘발유 가격',
    diesel_price INT COMMENT '경유 가격',
    premium_gasoline_price INT COMMENT '고급휘발유 가격',
    lpg_price INT COMMENT 'LPG 가격',

    -- 시스템 정보
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',

    UNIQUE KEY uk_station_date (station_id, price_date),
    INDEX idx_price_date (price_date),
    CONSTRAINT fk_gas_station_prices_station
        FOREIGN KEY (station_id)
        REFERENCES gas_stations(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='주유소 가격 정보';
