# QR 관리 서비스 개발 계획서

## 1. 서비스 개요

**서비스명**: QR 관리 서비스 (qrmanage)
**목적**: 상점이 메뉴 정보를 QR 코드로 제공하고, 고객이 스캔하여 메뉴를 조회하는 서비스

### 사용자 유형

| 유형 | 설명 |
|------|------|
| 최고관리자 | 전체 시스템 관리, 상점관리자 및 상점 검수 |
| 상점관리자 | 상점 정보/메뉴 관리, QR 코드 생성 |
| 일반고객 | QR 스캔하여 상점 정보/메뉴 조회 (비로그인) |

### URL 구조

```
/qr-manage/                     → 서비스 홈
/qr-manage/super/auth/login     → 최고관리자 로그인
/qr-manage/super/admin/...      → 최고관리자 기능
/qr-manage/shop/auth/login      → 상점관리자 로그인
/qr-manage/shop/admin/...       → 상점관리자 기능
/qr-manage/view/shop/{id}       → 고객용 상점 정보 페이지
/qr-manage/view/menu/{id}       → 고객용 메뉴 목록 페이지
```

---

## 2. 패키지 구조

```
src/main/java/co/grap/pack/qrmanage/
├── config/                          # 설정
│   ├── QrSecurityConfig.java        # Spring Security 설정
│   └── QrMyBatisConfig.java         # MyBatis 설정
│
├── superadmin/                      # 최고관리자 도메인
│   ├── auth/
│   │   ├── controller/QrSuperAuthController.java
│   │   ├── service/QrSuperAuthService.java
│   │   ├── mapper/QrSuperAdminMapper.java
│   │   └── model/QrSuperAdmin.java
│   │
│   ├── dashboard/
│   │   ├── controller/QrSuperDashboardController.java
│   │   └── service/QrSuperDashboardService.java
│   │
│   ├── shopadmin/                   # 상점관리자 관리
│   │   ├── controller/QrSuperShopAdminController.java
│   │   ├── service/QrSuperShopAdminService.java
│   │   ├── mapper/QrSuperShopAdminMapper.java
│   │   └── model/QrShopAdminSearchParam.java
│   │
│   ├── shop/                        # 상점 검수/관리
│   │   ├── controller/QrSuperShopController.java
│   │   ├── service/QrSuperShopService.java
│   │   ├── mapper/QrSuperShopMapper.java
│   │   └── model/
│   │       ├── QrShopReviewHistory.java
│   │       └── QrShopMemo.java
│   │
│   ├── qrcode/                      # QR 통합 관리
│   │   ├── controller/QrSuperQrCodeController.java
│   │   ├── service/QrSuperQrCodeService.java
│   │   └── mapper/QrSuperQrCodeMapper.java
│   │
│   ├── notification/                # 알림
│   │   ├── controller/QrSuperNotificationController.java
│   │   ├── service/QrSuperNotificationService.java
│   │   └── mapper/QrSuperNotificationMapper.java
│   │
│   └── log/                         # 활동 로그
│       ├── controller/QrSuperActivityLogController.java
│       ├── service/QrSuperActivityLogService.java
│       └── mapper/QrSuperActivityLogMapper.java
│
├── shopadmin/                       # 상점관리자 도메인
│   ├── auth/
│   │   ├── controller/QrShopAuthController.java
│   │   ├── service/QrShopAuthService.java
│   │   └── model/
│   │       ├── QrShopAdmin.java
│   │       └── QrShopAdminStatus.java  # enum: ACTIVE, INACTIVE, SUSPENDED
│   │
│   ├── dashboard/
│   │   ├── controller/QrShopDashboardController.java
│   │   └── service/QrShopDashboardService.java
│   │
│   ├── shop/                        # 상점 정보 관리
│   │   ├── controller/QrShopInfoController.java
│   │   ├── service/QrShopInfoService.java
│   │   ├── mapper/QrShopMapper.java
│   │   └── model/
│   │       ├── QrShop.java
│   │       ├── QrShopStatus.java    # enum: PENDING, APPROVED, REJECTED
│   │       └── QrBusinessHours.java
│   │
│   ├── category/                    # 카테고리 관리
│   │   ├── controller/QrCategoryController.java
│   │   ├── service/QrCategoryService.java
│   │   ├── mapper/QrCategoryMapper.java
│   │   └── model/QrMenuCategory.java
│   │
│   ├── menu/                        # 메뉴 관리
│   │   ├── controller/QrMenuController.java
│   │   ├── service/QrMenuService.java
│   │   ├── mapper/QrMenuMapper.java
│   │   └── model/
│   │       ├── QrMenu.java
│   │       ├── QrMenuOptionGroup.java
│   │       └── QrMenuOptionItem.java
│   │
│   ├── image/                       # 이미지 관리
│   │   ├── controller/QrImageController.java
│   │   ├── service/QrImageService.java
│   │   ├── mapper/QrImageMapper.java
│   │   └── model/QrImage.java
│   │
│   ├── qrcode/                      # 내 QR 관리
│   │   ├── controller/QrShopQrCodeController.java
│   │   ├── service/QrShopQrCodeService.java
│   │   └── model/
│   │       ├── QrCode.java
│   │       └── QrCodeType.java      # enum: SHOP, MENU
│   │
│   ├── stats/                       # 통계
│   │   ├── controller/QrShopStatsController.java
│   │   ├── service/QrShopStatsService.java
│   │   └── mapper/QrScanLogMapper.java
│   │
│   └── notification/                # 알림
│       ├── controller/QrShopNotificationController.java
│       └── service/QrShopNotificationService.java
│
├── customer/                        # 고객용 (비로그인)
│   ├── controller/QrCustomerViewController.java
│   ├── service/QrCustomerViewService.java
│   └── mapper/QrCustomerViewMapper.java
│
└── common/                          # 공통
    ├── model/
    │   ├── QrNotification.java
    │   ├── QrActivityLog.java
    │   └── QrScanLog.java
    ├── util/
    │   ├── QrCodeGenerator.java     # QR 생성 유틸
    │   └── ImageCompressor.java     # 이미지 압축 유틸
    └── service/
        └── QrNotificationService.java  # 공통 알림 서비스
```

---

## 3. 리소스 구조

### Templates

```
src/main/resources/templates/qrmanage/
├── super/                           # 최고관리자 페이지
│   ├── layout/
│   │   └── super-layout.html
│   ├── auth/
│   │   ├── login.html
│   │   └── reset-password.html
│   ├── dashboard/
│   │   └── index.html
│   ├── shopadmin/
│   │   ├── list.html
│   │   └── detail.html
│   ├── shop/
│   │   ├── list.html
│   │   ├── detail.html
│   │   └── review-form.html
│   ├── qrcode/
│   │   └── list.html
│   ├── notification/
│   │   └── list.html
│   └── log/
│       └── list.html
│
├── shop/                            # 상점관리자 페이지
│   ├── layout/
│   │   └── shop-layout.html
│   ├── auth/
│   │   ├── login.html
│   │   ├── register.html
│   │   ├── forgot-password.html
│   │   └── reset-password.html
│   ├── dashboard/
│   │   └── index.html
│   ├── info/
│   │   └── form.html
│   ├── category/
│   │   └── list.html
│   ├── menu/
│   │   ├── list.html
│   │   ├── form.html
│   │   └── option-form.html
│   ├── image/
│   │   └── list.html
│   ├── qrcode/
│   │   └── list.html
│   ├── stats/
│   │   └── index.html
│   └── notification/
│       └── list.html
│
└── customer/                        # 고객용 페이지
    ├── layout/
    │   └── customer-layout.html
    ├── shop-info.html               # 상점 정보
    └── menu-list.html               # 메뉴 목록
```

### Static Resources

```
src/main/resources/static/qrmanage/
├── css/
│   ├── super.css                    # 최고관리자 스타일
│   ├── shop.css                     # 상점관리자 스타일
│   └── customer.css                 # 고객용 스타일 (모바일 최적화)
├── js/
│   ├── super.js
│   ├── shop.js
│   └── customer.js
└── images/
    └── logo.png
```

### MyBatis Mappers

```
src/main/resources/mapper/qrmanage/
├── superadmin/
│   ├── QrSuperAdminMapper.xml
│   ├── QrSuperShopAdminMapper.xml
│   ├── QrSuperShopMapper.xml
│   ├── QrSuperQrCodeMapper.xml
│   ├── QrSuperNotificationMapper.xml
│   └── QrSuperActivityLogMapper.xml
├── shopadmin/
│   ├── QrShopMapper.xml
│   ├── QrCategoryMapper.xml
│   ├── QrMenuMapper.xml
│   ├── QrImageMapper.xml
│   ├── QrScanLogMapper.xml
│   └── QrShopNotificationMapper.xml
└── customer/
    └── QrCustomerViewMapper.xml
```

---

## 4. 데이터베이스 스키마

### 사용자 테이블

```sql
-- 최고관리자
CREATE TABLE qr_super_admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 상점관리자
CREATE TABLE qr_shop_admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    status VARCHAR(20) DEFAULT 'PENDING',  -- PENDING, ACTIVE, INACTIVE, SUSPENDED
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 상점 테이블

```sql
-- 상점
CREATE TABLE qr_shop (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_admin_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    address VARCHAR(255),
    phone VARCHAR(20),
    is_visible BOOLEAN DEFAULT FALSE,       -- 노출 여부 (최고관리자 관리)
    status VARCHAR(20) DEFAULT 'PENDING',   -- PENDING, APPROVED, REJECTED
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (shop_admin_id) REFERENCES qr_shop_admin(id)
);

-- 영업시간
CREATE TABLE qr_business_hours (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    day_of_week TINYINT NOT NULL,           -- 0=일, 1=월, ..., 6=토
    is_holiday BOOLEAN DEFAULT FALSE,       -- 휴무일 여부
    open_time TIME,
    close_time TIME,
    FOREIGN KEY (shop_id) REFERENCES qr_shop(id)
);

-- 상점 검수 이력
CREATE TABLE qr_shop_review_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,            -- 최고관리자 ID
    action VARCHAR(20) NOT NULL,            -- APPROVED, REJECTED
    comment TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (shop_id) REFERENCES qr_shop(id),
    FOREIGN KEY (reviewer_id) REFERENCES qr_super_admin(id)
);

-- 상점 메모 (최고관리자용)
CREATE TABLE qr_shop_memo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    admin_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (shop_id) REFERENCES qr_shop(id),
    FOREIGN KEY (admin_id) REFERENCES qr_super_admin(id)
);
```

### 메뉴 테이블

```sql
-- 메뉴 카테고리
CREATE TABLE qr_menu_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    sort_order INT DEFAULT 0,
    is_visible BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (shop_id) REFERENCES qr_shop(id)
);

-- 메뉴
CREATE TABLE qr_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    category_id BIGINT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price INT NOT NULL,
    is_visible BOOLEAN DEFAULT TRUE,
    is_sold_out BOOLEAN DEFAULT FALSE,
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (shop_id) REFERENCES qr_shop(id),
    FOREIGN KEY (category_id) REFERENCES qr_menu_category(id)
);

-- 메뉴 옵션 그룹 (예: "사이즈 선택", "맵기 선택")
CREATE TABLE qr_menu_option_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (menu_id) REFERENCES qr_menu(id)
);

-- 메뉴 옵션 항목 (예: "Small", "Medium", "Large")
CREATE TABLE qr_menu_option_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    option_group_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (option_group_id) REFERENCES qr_menu_option_group(id)
);
```

### QR 코드 테이블

```sql
-- QR 코드
CREATE TABLE qr_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    code_type VARCHAR(20) NOT NULL,         -- SHOP, MENU
    target_id BIGINT,                       -- SHOP이면 shop_id, MENU면 menu_id (null이면 전체메뉴)
    uuid VARCHAR(50) NOT NULL UNIQUE,       -- QR 식별용 UUID
    version INT DEFAULT 1,                  -- 재생성 시 증가
    is_active BOOLEAN DEFAULT TRUE,
    expires_at DATETIME,                    -- 만료일 (null이면 무제한)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (shop_id) REFERENCES qr_shop(id)
);

-- QR 스캔 로그
CREATE TABLE qr_scan_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    qr_code_id BIGINT NOT NULL,
    scanned_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50),
    user_agent TEXT,
    FOREIGN KEY (qr_code_id) REFERENCES qr_code(id)
);
```

### 이미지 테이블

```sql
-- 이미지 (상점/메뉴 공용)
CREATE TABLE qr_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    target_type VARCHAR(20) NOT NULL,       -- SHOP, MENU
    target_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size INT,
    is_primary BOOLEAN DEFAULT FALSE,       -- 대표 이미지 여부
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 시스템 테이블

```sql
-- 알림
CREATE TABLE qr_notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    target_type VARCHAR(20) NOT NULL,       -- SUPER_ADMIN, SHOP_ADMIN
    target_id BIGINT NOT NULL,              -- 수신자 ID
    title VARCHAR(200) NOT NULL,
    content TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 활동 로그
CREATE TABLE qr_activity_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor_type VARCHAR(20) NOT NULL,        -- SUPER_ADMIN, SHOP_ADMIN
    actor_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,           -- 예: SHOP_CREATED, MENU_UPDATED
    target_type VARCHAR(50),                -- 대상 타입 (SHOP, MENU 등)
    target_id BIGINT,                       -- 대상 ID
    detail TEXT,                            -- 상세 내용 (JSON)
    ip_address VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

---

## 5. 개발 단계

### 1단계: 기본 구조 세팅
- [ ] qrmanage 패키지 생성
- [ ] application-qrmanage.yml 설정 파일 생성
- [ ] QrSecurityConfig 설정 (최고관리자/상점관리자 분리 인증)
- [ ] QrMyBatisConfig 설정
- [ ] 기본 레이아웃 템플릿 생성

### 2단계: 인증 시스템
- [ ] 최고관리자 로그인
- [ ] 상점관리자 로그인/회원가입
- [ ] 비밀번호 찾기/재설정
- [ ] 상점관리자 상태 관리 (PENDING → ACTIVE 승인 플로우)

### 3단계: 최고관리자 - 상점관리자 관리
- [ ] 상점관리자 목록/검색
- [ ] 상점관리자 상세/승인/정지
- [ ] 상점관리자 계정 직접 생성

### 4단계: 상점 관리
- [ ] 상점관리자: 상점 정보 등록/수정
- [ ] 상점관리자: 영업시간/휴무일 설정
- [ ] 최고관리자: 상점 목록/검색
- [ ] 최고관리자: 상점 검수 (승인/반려)
- [ ] 최고관리자: 상점 노출 관리
- [ ] 최고관리자: 상점 메모/이력

### 5단계: 카테고리 + 메뉴 관리
- [ ] 카테고리 CRUD
- [ ] 카테고리 순서 정렬
- [ ] 카테고리 공개/비공개
- [ ] 메뉴 CRUD
- [ ] 메뉴 순서 정렬
- [ ] 메뉴 공개/비공개/품절
- [ ] 메뉴 옵션 그룹/항목 관리

### 6단계: 이미지 관리
- [ ] 이미지 업로드
- [ ] 이미지 압축 (용량 최적화)
- [ ] 대표 이미지 설정
- [ ] 이미지 순서 정렬

### 7단계: QR 코드
- [ ] 상점 대표 QR 생성
- [ ] 메뉴 QR 자동 생성
- [ ] QR 목록/다운로드
- [ ] QR 재생성 (기존 무효화)
- [ ] QR 만료 설정
- [ ] 최고관리자: QR 통합 관리

### 8단계: 고객용 페이지
- [ ] 상점 정보 페이지 (대표 QR 스캔)
- [ ] 메뉴 목록 페이지 (메뉴 QR 스캔)
- [ ] 모바일 반응형 디자인
- [ ] QR 스캔 로그 기록

### 9단계: 통계 + 알림 + 로그
- [ ] QR 스캔 통계 (상점관리자용)
- [ ] 대시보드 통계 (양쪽)
- [ ] 알림 시스템 (벨 아이콘)
- [ ] 활동 로그 기록/조회

### 10단계: UX 개선
- [ ] 검색/필터 기능
- [ ] 페이지네이션
- [ ] 드래그 앤 드롭 순서 정렬
- [ ] 전체 모바일 최적화 점검

---

## 6. 기술 스택

| 항목 | 기술 |
|------|------|
| Backend | Spring Boot 3.2.0, Spring Security, MyBatis |
| Database | MariaDB/MySQL |
| Frontend | Thymeleaf, HTMX, CSS |
| QR 생성 | ZXing 라이브러리 |
| 이미지 압축 | Thumbnailator 또는 ImageIO |
| Build | Gradle |

---

## 7. 네이밍 규칙

기존 grap 서비스와 일관성 유지:

| 항목 | 규칙 | 예시 |
|------|------|------|
| 패키지 | `co.grap.pack.qrmanage.{domain}` | `qrmanage.shopadmin.menu` |
| 클래스 | `QrManage` 접두사 | `QrManageMenuService`, `QrManageShopController` |
| 테이블 | `qr_manage_` 접두사 | `qr_manage_shop`, `qr_manage_menu` |
| URL | `/qr-manage/` 시작 | `/qr-manage/shop/admin/menu/list` |
| CSS 클래스 | `qr-manage-` 접두사 | `qr-manage-btn`, `qr-manage-card` |

---

## 8. 예상 파일 수

| 구분 | 파일 수 (예상) |
|------|---------------|
| Java 클래스 | ~60개 |
| MyBatis XML | ~15개 |
| Thymeleaf 템플릿 | ~30개 |
| CSS/JS | ~6개 |
| 설정 파일 | ~2개 |

---

## 9. 참고사항

- grap 서비스와 동일한 프로젝트 내에서 운영
- 별도의 인증 체계 (최고관리자 ≠ grap 관리자)
- 데이터베이스는 공유하되 테이블은 `qr_` 접두사로 구분
