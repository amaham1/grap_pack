# Grap CMS (Content Management System)

스프링부트, Thymeleaf, HTMX, MyBatis, MariaDB를 사용한 콘텐츠 관리 시스템

## 기술 스택

- **Backend**: Spring Boot 3.2.0, Spring Security, MyBatis
- **Frontend**: Thymeleaf, HTMX
- **Database**: MariaDB
- **Build Tool**: Gradle
- **Java**: 17
- **Package**: `co.grap.pack`

## 멀티 서비스 구조

이 프로젝트는 하나의 애플리케이션에서 여러 서비스를 운영할 수 있는 구조입니다:
- **Grap CMS** (`/grap`): 현재 구현된 콘텐츠 관리 서비스
- **QR 관리** (`/qr-manage`): 향후 추가될 QR 관리 서비스 (별도 인증 체계)

## 주요 기능

### 관리자 기능
- 관리자 로그인/로그아웃
- 콘텐츠 종류 관리 (등록/수정/삭제/활성화)
- 콘텐츠 관리 (등록/수정/삭제/검색/페이징)
- 이미지 업로드 (콘텐츠당 최대 10개)
- 콘텐츠 공개/비공개 설정
- 외부 API 동기화 (축제, 공연/전시, 복지서비스, 주유소)

### 사용자 기능
- 공개된 콘텐츠 목록 조회 (페이징)
- 콘텐츠 상세 조회
- 콘텐츠 검색
- HTMX를 통한 동적 페이지 로딩

## 프로젝트 구조

```
src/main/java/co/grap/pack/
├── auth/                    # 인증 도메인
│   ├── controller/
│   ├── service/
│   ├── model/
│   └── mapper/
├── admin/                   # 관리자 영역
│   ├── content/            # 콘텐츠 관리
│   ├── image/              # 이미지 관리
│   ├── external/           # 외부 데이터 관리
│   └── sync/               # API 동기화
├── user/                    # 사용자 영역
│   └── content/            # 콘텐츠 조회
├── external/                # 외부 API 연동
├── home/                    # 홈 컨트롤러
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
java -jar build/libs/pack-0.0.1-SNAPSHOT.jar
```

### 4. 접속

- 관리자 페이지: http://localhost:8080/grap/auth/login
- 사용자 페이지: http://localhost:8080/grap/user/content/festivals

## 코딩 규칙

- **네이밍**: camelCase 사용
- **주석**: 한글 주석 사용
- **Lombok**: @Data, @Builder, @RequiredArgsConstructor 활용
- **트랜잭션**: Service 계층에서 @Transactional 사용
- **MyBatis**: Dynamic SQL 적극 활용

## API 엔드포인트

### 관리자
- `GET /grap/auth/login` - 로그인 페이지
- `POST /grap/auth/login` - 로그인 처리
- `POST /grap/auth/logout` - 로그아웃
- `GET /grap/admin/content-type/list` - 콘텐츠 종류 목록
- `GET /grap/admin/content-type/form` - 콘텐츠 종류 등록/수정 폼
- `POST /grap/admin/content-type/create` - 콘텐츠 종류 등록
- `POST /grap/admin/content-type/update` - 콘텐츠 종류 수정
- `POST /grap/admin/content-type/delete/{id}` - 콘텐츠 종류 삭제
- `GET /grap/admin/content/list` - 콘텐츠 목록
- `GET /grap/admin/content/form` - 콘텐츠 등록/수정 폼
- `POST /grap/admin/content/create` - 콘텐츠 등록
- `POST /grap/admin/content/update` - 콘텐츠 수정
- `POST /grap/admin/content/delete/{id}` - 콘텐츠 삭제
- `POST /grap/admin/content/publish/{id}` - 콘텐츠 공개/비공개
- `POST /grap/admin/image/upload` - 이미지 업로드
- `POST /grap/admin/image/delete/{id}` - 이미지 삭제
- `GET /grap/admin/sync` - 외부 API 동기화 페이지
- `GET /grap/admin/external/festivals` - 축제/행사 관리
- `GET /grap/admin/external/exhibitions` - 공연/전시 관리
- `GET /grap/admin/external/welfare` - 복지서비스 관리
- `GET /grap/admin/external/gas-stations` - 주유소 관리

### 사용자
- `GET /grap/user/content/list` - 콘텐츠 목록
- `GET /grap/user/content/detail/{id}` - 콘텐츠 상세
- `GET /grap/user/content/festivals` - 축제/행사 목록
- `GET /grap/user/content/exhibitions` - 공연/전시 목록
- `GET /grap/user/content/welfare` - 복지서비스 목록
- `GET /grap/user/content/gas-stations` - 주유소 목록

## 라이센스

MIT License
