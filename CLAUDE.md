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

## Claude Code 협업 워크플로우

### 기능 구현 및 검증 프로세스

Claude Code와 효율적으로 협업하기 위한 표준 워크플로우:

#### 1. 기능 구현 (Claude)
- 요구사항에 맞게 코드 작성
- 필요한 Service, Controller, Mapper, Model 구현

#### 2. 검증 로그 추가 (Claude)
- 기능의 핵심 지점에 검증용 로그 추가
- 패턴화된 로그 메시지 사용으로 필터링 용이하게 구성
```java
log.info("✅ [CHECK] 사용자 권한 검증 완료");
log.info("✅ [CHECK] 콘텐츠 저장 성공: contentId={}", contentId);
log.error("❌ [ERROR] 파일 업로드 실패: {}", e.getMessage());
```

#### 3. 서버 실행 + 사용자 테스트 + 로그 확인 (협업)
- **Claude**: 백그라운드로 서버 실행 및 로그 모니터링
- **사용자**: 브라우저에서 실제 UI/기능 테스트
- **Claude**: 필터링된 로그로 기능 정상 작동 검증

#### 로그 레벨 최적화 (토큰 효율성)

검증 시 불필요한 로그를 최소화하여 토큰 사용량을 줄입니다:

```yaml
# application.yml - 검증용 로그 설정
logging:
  level:
    root: WARN                    # 기본 로그 레벨 최소화
    com.grap: INFO                # 프로젝트 로그만 INFO
    org.springframework: ERROR    # Spring Framework는 에러만
    org.mybatis: ERROR            # MyBatis는 에러만
```

#### 로그 필터링

백그라운드 실행 시 필요한 로그만 추출:
```bash
# 검증 로그와 에러만 확인
./gradlew bootRun | grep -E "\[CHECK\]|\[ERROR\]|Exception"

# 또는 특정 패턴만
./gradlew bootRun | grep "✅"
```

#### 장점
- ✅ **실제 사용자 시나리오 테스트**: JWT 인증, 복잡한 UI 상호작용 등 실제 환경에서 검증
- ✅ **토큰 효율성**: 필요한 로그만 확인하여 토큰 소비 최소화
- ✅ **빠른 피드백**: 에러 즉시 포착 및 수정
- ✅ **협업 효율성**: Claude는 코드와 로그, 사용자는 UX 검증

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
- **네이밍**: 네이밍은 중복되지 않고 명확성있게 작성바람

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
