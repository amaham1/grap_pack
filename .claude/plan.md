# 클래스 네이밍 리팩토링 계획

## 목적
사용자(User)와 관리자(Admin) 도메인의 클래스들을 명확하게 구분하고, External API 모델들도 명확한 prefix를 추가하여 코드 가독성과 유지보수성을 향상시킵니다.

## 변경 대상

### 1. Admin Content 도메인
- Content → AdminContent
- ContentSearchParam → AdminContentSearchParam
- ContentType → AdminContentType

### 2. Admin Image 도메인
- Image → AdminImage

### 3. User Content 도메인
- Content → UserContent
- ContentSearchParam → UserContentSearchParam

### 4. External API 모델
- Festival → ExternalFestival
- Exhibition → ExternalExhibition
- GasStation → ExternalGasStation
- GasPrice → ExternalGasPrice
- WelfareService → ExternalWelfareService

### 5. External API Mapper
- FestivalMapper → ExternalFestivalMapper
- ExhibitionMapper → ExternalExhibitionMapper
- GasStationMapper → ExternalGasStationMapper
- GasPriceMapper → ExternalGasPriceMapper
- WelfareServiceMapper → ExternalWelfareServiceMapper

## 작업 단계

### Phase 1: Admin Content 도메인
1. Model 클래스명 변경 (Content, ContentType, ContentSearchParam)
2. 모든 import 문 업데이트
3. Controller, Service, Mapper 타입 참조 업데이트
4. 빌드 테스트

### Phase 2: Admin Image 도메인
1. Image 클래스명 변경
2. import 문 업데이트 (UserContentService 포함)
3. 빌드 테스트

### Phase 3: User Content 도메인
1. Model 클래스명 변경 (Content, ContentSearchParam)
2. User 도메인 파일들 업데이트
3. 빌드 테스트

### Phase 4: External API
1. Model 클래스명 변경
2. Mapper 인터페이스명 변경
3. XML 매퍼 파일명 및 namespace 변경
4. API Service 클래스 업데이트
5. 빌드 테스트

### Phase 5: 최종 검증
1. 전체 빌드 실행
2. 주요 기능 동작 확인

## 예상 영향
총 약 90개 이상의 파일이 수정될 예정입니다.