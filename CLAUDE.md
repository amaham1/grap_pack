# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 3.2.0 기반의 Content Management System (CMS). MyBatis를 ORM으로 사용하고, Thymeleaf + HTMX로 동적 UI를 구성합니다.

**Tech Stack**: Spring Boot 3.2.0, Spring Security, MyBatis, MariaDB/MySQL, Thymeleaf, HTMX, Gradle, Java 17

## Development Commands

### Build and Run
```bash
./gradlew build
./gradlew bootRun
```

### Testing
```bash
./gradlew test
./gradlew test --tests ClassName.methodName
```

### Clean Build
```bash
./gradlew clean build
```

## Architecture

### Domain-Driven Structure

프로젝트는 도메인 중심으로 구성되어 있으며, 각 도메인은 Controller-Service-Mapper-Model 레이어를 포함합니다:

- **auth**: 관리자 인증/로그인 처리
- **admin**: 관리자 기능 (콘텐츠 관리, 이미지 관리)
- **user**: 사용자 기능 (콘텐츠 조회)
- **common**: 공통 설정 및 유틸리티

### MyBatis Integration

MyBatis XML 매퍼는 `src/main/resources/mapper/` 디렉토리에 도메인별로 구성되어 있습니다. Mapper 인터페이스는 `@MapperScan`을 통해 자동 스캔됩니다 (MyBatisConfig.java:16-21).
**중요**: Type aliases는 `MyBatisConfig.java`에서 설정되며 (`setTypeAliasesPackage`), `application.yml`에서는 중복 설정하지 않습니다.

### Spring Security Configuration

SecurityConfig.java에서 다음을 정의합니다:
- `/admin/**` 경로는 인증 필요
- `/auth/**`, `/user/**`, 정적 리소스는 모두 허용
- Form 기반 로그인: `/auth/login`에서 처리
- 로그인 성공 시 `/admin/content/list`로 리다이렉트
- Session 관리: 동시 세션 1개로 제한

### Authentication Flow

AuthService는 Spring Security의 `UserDetailsService`를 구현하여 인증을 처리합니다:
1. 사용자명으로 DB에서 Admin 조회 (AuthMapper)
2. BCrypt로 암호화된 비밀번호 검증
3. Spring Security UserDetails 객체 반환

### Schema
주요 테이블:
src\main\resources\sql\schema.sql 분석

## Key Configuration
### Thymeleaf
- Template prefix: `classpath:/templates/`
- Cache: 비활성화 (개발 편의)
- Encoding: UTF-8
- Layout 시스템: `admin/layout/admin-layout.html`, `user/layout/user-layout.html`

## Coding Conventions

- **한글 주석**: 모든 클래스, 메서드에 한글 주석 사용
- **Lombok**: `@RequiredArgsConstructor`, `@Data`, `@Builder` 활용
- **Transactions**: Service 레이어에서 `@Transactional` 사용, 읽기 전용 쿼리는 `@Transactional(readOnly = true)`
- **MyBatis Dynamic SQL**: `<if>`, `<choose>`, `<foreach>` 등 적극 활용
- **파라미터 어노테이션**: `@PathVariable`, `@RequestParam`에 반드시 명시적인 이름 지정
  - 올바른 예: `@PathVariable("contentId") Long contentId`
  - 잘못된 예: `@PathVariable Long contentId` (컴파일 시 파라미터 이름 정보 손실 가능)

## Common Patterns

### Service Layer Pattern
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SomeService {
    private final SomeMapper mapper;

    @Transactional  // 쓰기 작업만 별도 트랜잭션
    public void create(...) { ... }
}
```

### Controller-Service-Mapper Flow
1. Controller: HTTP 요청 수신, 파라미터 바인딩
2. Service: 비즈니스 로직 처리, 트랜잭션 관리
3. Mapper: MyBatis XML 매퍼를 통한 DB 접근
4. Model: DTO/Entity 객체 (Lombok으로 보일러플레이트 제거)

### HTMX Usage
User-facing 페이지에서 HTMX를 사용하여 부분 페이지 업데이트 구현. 관리자 페이지는 전통적인 form submit 방식 사용.

### Thymeleaf 주의사항
- **정적 클래스 접근 금지**: `T(System)`, `T(Class)` 등 정적 클래스 접근은 Thymeleaf에서 제한됨
  - 잘못된 예: `T(System).lineSeparator()`
  - 올바른 예: `\n`을 직접 사용하거나 Controller에서 미리 처리
- **HTMX 속성**: HTMX 속성에 Thymeleaf URL 표현식을 사용할 때는 `th:` 접두사 필요
  - 올바른 예: `th:hx-get="@{/url}"`
  - 잘못된 예: `hx-get="@{/url}"`
