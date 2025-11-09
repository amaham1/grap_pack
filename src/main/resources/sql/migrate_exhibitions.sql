-- exhibitions 테이블에 누락된 컬럼 추가
-- 기존 데이터를 유지하면서 스키마를 최신 버전으로 업데이트합니다.

-- 기존에 없던 컬럼들 추가
ALTER TABLE exhibitions
    ADD COLUMN IF NOT EXISTS category_name VARCHAR(100) COMMENT '카테고리명 (전시, 공연, 행사 등)' AFTER title,
    ADD COLUMN IF NOT EXISTS cover_image_url VARCHAR(1000) COMMENT '커버 이미지 URL' AFTER category_name,
    ADD COLUMN IF NOT EXISTS location_name VARCHAR(500) COMMENT '장소명' AFTER time_info,
    ADD COLUMN IF NOT EXISTS organizer_info VARCHAR(500) COMMENT '주최/주관 정보' AFTER location_name,
    ADD COLUMN IF NOT EXISTS tel_number VARCHAR(100) COMMENT '문의 전화번호' AFTER organizer_info,
    ADD COLUMN IF NOT EXISTS pay_info VARCHAR(500) COMMENT '요금 정보' AFTER tel_number,
    ADD COLUMN IF NOT EXISTS status_info VARCHAR(200) COMMENT '상태 정보' AFTER pay_info,
    ADD COLUMN IF NOT EXISTS division_name VARCHAR(200) COMMENT '구분명' AFTER status_info,
    ADD COLUMN IF NOT EXISTS api_raw_data JSON COMMENT 'API 원본 데이터 (JSON)' AFTER division_name;

-- 기존 컬럼 타입 변경 (필요한 경우)
ALTER TABLE exhibitions
    MODIFY COLUMN start_date DATETIME COMMENT '시작일',
    MODIFY COLUMN end_date DATETIME COMMENT '종료일',
    MODIFY COLUMN time_info VARCHAR(500) COMMENT '운영 시간 정보';

-- 기존 컬럼 삭제 (새 스키마에 없는 컬럼들)
ALTER TABLE exhibitions
    DROP COLUMN IF EXISTS location,
    DROP COLUMN IF EXISTS location_detail,
    DROP COLUMN IF EXISTS content_html,
    DROP COLUMN IF EXISTS price_info,
    DROP COLUMN IF EXISTS inquiry_info,
    DROP COLUMN IF EXISTS source_url,
    DROP COLUMN IF EXISTS files_info;

-- 인덱스 추가
CREATE INDEX IF NOT EXISTS idx_category_name ON exhibitions(category_name);
CREATE INDEX IF NOT EXISTS idx_start_date ON exhibitions(start_date);
CREATE INDEX IF NOT EXISTS idx_end_date ON exhibitions(end_date);
