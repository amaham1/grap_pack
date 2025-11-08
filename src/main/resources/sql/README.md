# GRAP CMS Database Setup Guide

이 디렉토리에는 GRAP CMS 애플리케이션을 위한 MySQL 데이터베이스 스키마와 초기 데이터가 포함되어 있습니다.

## 파일 설명

- **schema.sql**: 데이터베이스 스키마 정의 (테이블 생성)
- **data.sql**: 초기 샘플 데이터
- **README.md**: 이 파일

## 데이터베이스 설치 방법

### 1. MySQL 명령줄 사용

```bash
# 스키마 생성
mysql -u root -p < src/main/resources/sql/schema.sql

# 초기 데이터 입력
mysql -u root -p < src/main/resources/sql/data.sql
```

### 2. MySQL Workbench 사용

1. MySQL Workbench 실행
2. File > Open SQL Script 선택
3. `schema.sql` 파일 열기
4. Execute 버튼 (⚡) 클릭
5. 동일한 방법으로 `data.sql` 실행

### 3. DBeaver 사용

1. DBeaver에서 데이터베이스 연결
2. SQL Editor 열기
3. `schema.sql` 파일 내용 붙여넣기
4. Execute SQL Statement (Ctrl+Enter)
5. 동일한 방법으로 `data.sql` 실행

### 4. Docker 컨테이너 내에서 실행

```bash
# Docker 컨테이너로 SQL 파일 복사
docker cp src/main/resources/sql/schema.sql mysql-container:/tmp/
docker cp src/main/resources/sql/data.sql mysql-container:/tmp/

# Docker 컨테이너 내에서 실행
docker exec -i mysql-container mysql -uroot -proot < /tmp/schema.sql
docker exec -i mysql-container mysql -uroot -proot < /tmp/data.sql
```

## 테이블 구조

### 1. admin (관리자)
시스템 관리자 정보 저장

**주요 컬럼:**
- admin_id: 관리자 ID (PK)
- username: 로그인 사용자명
- password: 암호화된 비밀번호
- name: 관리자 이름

### 2. content_type (콘텐츠 타입)
콘텐츠의 카테고리/유형 정의

**주요 컬럼:**
- content_type_id: 콘텐츠 타입 ID (PK)
- type_name: 타입명 (공지사항, 뉴스, 블로그 등)
- type_desc: 타입 설명
- is_active: 활성화 여부

### 3. content (콘텐츠)
실제 콘텐츠 데이터 저장

**주요 컬럼:**
- content_id: 콘텐츠 ID (PK)
- content_type_id: 콘텐츠 타입 ID (FK)
- title: 제목
- content: 내용
- is_published: 공개 여부
- view_count: 조회수

### 4. image (이미지)
콘텐츠에 첨부된 이미지 정보

**주요 컬럼:**
- image_id: 이미지 ID (PK)
- content_id: 콘텐츠 ID (FK)
- original_name: 원본 파일명
- saved_name: 저장된 파일명
- file_path: 파일 경로
- file_size: 파일 크기
- display_order: 표시 순서

## 공통 컬럼 (모든 테이블)

모든 테이블에는 다음 공통 컬럼이 포함되어 있습니다:

- **create_dt**: 생성일시 (자동 생성)
- **create_name**: 생성자명
- **update_dt**: 수정일시 (자동 업데이트)
- **update_name**: 수정자명
- **delete_dt**: 삭제일시 (Soft Delete용)
- **delete_name**: 삭제자명

## 관계도

```
                    content_type (1)
                          |
                          | (FK)
                          ↓
admin (1) ----< content (N) ----< image (N)
           (생성자 관계)      (콘텐츠-이미지 관계)
```

## 초기 계정 정보

**관리자 계정**
- ID: admin
- Password: admin123

**에디터 계정**
- ID: editor
- Password: admin123

> ⚠️ **보안 경고**: 프로덕션 환경에서는 반드시 비밀번호를 변경하세요!

## 주요 특징

1. **UTF-8 완벽 지원**: utf8mb4_unicode_ci 문자셋 사용
2. **Soft Delete**: delete_dt, delete_name을 통한 논리 삭제 지원
3. **감사 추적**: 모든 생성/수정/삭제 이력 추적
4. **외래키 제약조건**: 데이터 무결성 보장
5. **인덱스 최적화**: 검색 성능 향상

## 데이터베이스 초기화

기존 데이터를 모두 삭제하고 처음부터 다시 시작하려면:

```bash
mysql -u root -p < src/main/resources/sql/schema.sql
mysql -u root -p < src/main/resources/sql/data.sql
```

## Soft Delete 사용법

데이터를 실제로 삭제하지 않고 논리적으로 삭제 표시:

```sql
-- 콘텐츠 삭제 (Soft Delete)
UPDATE content
SET delete_dt = NOW(), delete_name = 'admin'
WHERE content_id = 1;

-- 삭제되지 않은 콘텐츠만 조회
SELECT * FROM content WHERE delete_dt IS NULL;

-- 삭제된 콘텐츠 조회
SELECT * FROM content WHERE delete_dt IS NOT NULL;
```

## 트러블슈팅

### 외래키 제약조건 오류 발생 시

```sql
SET FOREIGN_KEY_CHECKS = 0;
-- 테이블 삭제 또는 데이터 삭제
SET FOREIGN_KEY_CHECKS = 1;
```

### 문자셋 문제 발생 시

데이터베이스 문자셋 확인:
```sql
SHOW VARIABLES LIKE 'char%';
```

테이블 문자셋 확인:
```sql
SHOW CREATE TABLE admin;
```

### RSA 인증 오류 발생 시 (MySQL 8.0)

```bash
mysql -u root -p --get-server-public-key
```

또는 사용자 인증 방식 변경:
```sql
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
FLUSH PRIVILEGES;
```
