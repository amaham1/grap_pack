# CMS (Content Management System)

스프링부트, Thymeleaf, HTMX, MyBatis, MariaDB를 사용한 콘텐츠 관리 시스템

## 기술 스택

- **Backend**: Spring Boot 3.2.0, Spring Security, MyBatis
- **Frontend**: Thymeleaf, HTMX
- **Database**: MariaDB
- **Build Tool**: Gradle
- **Java**: 17

## 주요 기능

### 관리자 기능
- 관리자 로그인/로그아웃
- 콘텐츠 종류 관리 (등록/수정/삭제/활성화)
- 콘텐츠 관리 (등록/수정/삭제/검색/페이징)
- 이미지 업로드 (콘텐츠당 최대 10개)
- 콘텐츠 공개/비공개 설정

### 사용자 기능
- 공개된 콘텐츠 목록 조회 (페이징)
- 콘텐츠 상세 조회
- 콘텐츠 검색
- HTMX를 통한 동적 페이지 로딩

## 프로젝트 구조

```
src/main/java/com/example/cms/
├── auth/                    # 인증 도메인
│   ├── controller/
│   ├── service/
│   ├── model/
│   └── mapper/
├── admin/                   # 관리자 영역
│   ├── content/            # 콘텐츠 관리
│   └── image/              # 이미지 관리
├── user/                    # 사용자 영역
│   └── content/            # 콘텐츠 조회
└── common/                  # 공통
    ├── config/
    └── util/

src/main/resources/
├── mapper/                  # MyBatis XML
├── templates/               # Thymeleaf 템플릿
│   ├── admin/              # 관리자 페이지
│   └── user/               # 사용자 페이지
└── static/                  # 정적 리소스
    ├── css/
    ├── js/
    └── uploads/            # 업로드 이미지
```

## 데이터베이스 설정

### 1. MariaDB 데이터베이스 생성

```sql
CREATE DATABASE cms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 테이블 생성

```sql
-- 관리자 테이블
CREATE TABLE admin (
    admin_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 콘텐츠 종류 테이블
CREATE TABLE content_type (
    content_type_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(50) NOT NULL UNIQUE,
    type_desc VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 콘텐츠 테이블
CREATE TABLE content (
    content_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content_type_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    is_published BOOLEAN DEFAULT FALSE,
    view_count INT DEFAULT 0,
    created_by BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (content_type_id) REFERENCES content_type(content_type_id),
    FOREIGN KEY (created_by) REFERENCES admin(admin_id)
);

-- 이미지 테이블
CREATE TABLE image (
    image_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content_id BIGINT NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    saved_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    display_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (content_id) REFERENCES content(content_id) ON DELETE CASCADE
);
```

### 3. 초기 관리자 계정 생성

```sql
-- 비밀번호: admin (BCrypt 암호화 필요)
INSERT INTO admin (username, password, name)
VALUES ('admin', '$2a$10$XPJ5Jq5YqXvW5Y5YqXvW5e5YqXvW5Y5YqXvW5Y5YqXvW5Y5YqXvW5', '관리자');
```

## 실행 방법

### 1. 환경 설정

`src/main/resources/application.yml` 파일에서 데이터베이스 연결 정보를 수정합니다:

```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/cms
    username: root
    password: your_password
```

### 2. 업로드 디렉토리 생성

```bash
mkdir -p /uploads
```

또는 `application.yml`에서 `file.upload-dir` 경로를 변경합니다.

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```

또는

```bash
./gradlew build
java -jar build/libs/cms-0.0.1-SNAPSHOT.jar
```

### 4. 접속

- 관리자 페이지: http://localhost:8080/auth/login
- 사용자 페이지: http://localhost:8080/user/content/list

## 코딩 규칙

- **네이밍**: camelCase 사용
- **주석**: 한글 주석 사용
- **Lombok**: @Data, @Builder, @RequiredArgsConstructor 활용
- **트랜잭션**: Service 계층에서 @Transactional 사용
- **MyBatis**: Dynamic SQL 적극 활용

## API 엔드포인트

### 관리자
- `GET /auth/login` - 로그인 페이지
- `POST /auth/login` - 로그인 처리
- `POST /auth/logout` - 로그아웃
- `GET /admin/content-type/list` - 콘텐츠 종류 목록
- `GET /admin/content-type/form` - 콘텐츠 종류 등록/수정 폼
- `POST /admin/content-type/create` - 콘텐츠 종류 등록
- `POST /admin/content-type/update` - 콘텐츠 종류 수정
- `POST /admin/content-type/delete/{id}` - 콘텐츠 종류 삭제
- `GET /admin/content/list` - 콘텐츠 목록
- `GET /admin/content/form` - 콘텐츠 등록/수정 폼
- `POST /admin/content/create` - 콘텐츠 등록
- `POST /admin/content/update` - 콘텐츠 수정
- `POST /admin/content/delete/{id}` - 콘텐츠 삭제
- `POST /admin/content/publish/{id}` - 콘텐츠 공개/비공개
- `POST /admin/image/upload` - 이미지 업로드
- `POST /admin/image/delete/{id}` - 이미지 삭제

### 사용자
- `GET /user/content/list` - 콘텐츠 목록
- `GET /user/content/detail/{id}` - 콘텐츠 상세

## 라이센스

MIT License
