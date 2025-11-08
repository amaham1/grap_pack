# CMS Database Setup Guide

이 디렉토리에는 CMS 애플리케이션을 위한 MySQL/MariaDB 데이터베이스 스키마와 초기 데이터가 포함되어 있습니다.

## 파일 설명

- **schema.sql**: 데이터베이스 스키마 정의 (테이블 생성)
- **data.sql**: 초기 샘플 데이터
- **README.md**: 이 파일

## 데이터베이스 설치 방법

### 1. MySQL/MariaDB 명령줄 사용

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
- 시스템 관리자 정보 저장
- 로그인 인증에 사용

### 2. content_type (콘텐츠 타입)
- 콘텐츠의 카테고리/유형 정의
- 공지사항, 뉴스, 블로그, 이벤트, FAQ 등

### 3. content (콘텐츠)
- 실제 콘텐츠 데이터 저장
- 제목, 내용, 공개여부, 조회수 등

### 4. image (이미지)
- 콘텐츠에 첨부된 이미지 정보
- 파일명, 경로, 크기, 표시순서 등

## 관계도

```
admin (1) ----< (N) content (1) ----< (N) image
                      ↓ (N)
                      |
                     (1)
                content_type
```

## 초기 계정 정보

**관리자 계정**
- ID: admin
- Password: admin123

**에디터 계정**
- ID: editor
- Password: admin123

> ⚠️ **보안 경고**: 프로덕션 환경에서는 반드시 비밀번호를 변경하세요!

## 주의사항

1. `schema.sql` 실행 시 기존 테이블이 삭제됩니다 (DROP TABLE)
2. 데이터베이스명: `cms`
3. 문자셋: `utf8mb4_unicode_ci`
4. 외래키 제약조건이 설정되어 있어 데이터 삭제 시 순서에 유의해야 합니다

## 데이터베이스 초기화

기존 데이터를 모두 삭제하고 처음부터 다시 시작하려면:

```bash
mysql -u root -p < src/main/resources/sql/schema.sql
mysql -u root -p < src/main/resources/sql/data.sql
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
