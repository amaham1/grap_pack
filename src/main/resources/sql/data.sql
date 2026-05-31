-- ============================================
-- GRAP CMS Initial Data for MySQL
-- ============================================

USE grap_cms;

-- ============================================
-- 1. 관리자 초기 데이터
-- ============================================
-- 비밀번호: admin123 (실제로는 BCrypt로 암호화 필요)
INSERT INTO admin (username, password, name, create_name) VALUES
('admin', '$2a$12$IHOlv8HF59gDoxSawFpSdOLACJjPbC5uxFUoI.PT.W66rj7gk1YCq', '시스템 관리자', 'SYSTEM'),
('editor', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '에디터', 'SYSTEM');

-- ============================================
-- 2. 콘텐츠 타입 초기 데이터
-- ============================================
INSERT INTO content_type (type_name, type_desc, is_active, create_name) VALUES
('공지사항', '중요한 공지사항 게시물', TRUE, 'SYSTEM'),
('뉴스', '최신 뉴스 및 소식', TRUE, 'SYSTEM'),
('블로그', '일반 블로그 포스트', TRUE, 'SYSTEM'),
('이벤트', '진행중이거나 예정된 이벤트', TRUE, 'SYSTEM'),
('FAQ', '자주 묻는 질문', TRUE, 'SYSTEM');

-- ============================================
-- 3. 콘텐츠 샘플 데이터
-- ============================================
INSERT INTO content (content_type_id, title, content, is_published, view_count, create_name) VALUES
-- 공지사항
(1, '시스템 점검 안내', '<h2>시스템 점검 안내</h2><p>2024년 1월 15일 02:00 ~ 06:00 시스템 점검이 진행될 예정입니다.</p><p>점검 시간 동안 서비스 이용이 불가능하오니 양해 부탁드립니다.</p>', TRUE, 150, 'admin'),
(1, '새로운 기능 업데이트', '<h2>새로운 기능이 추가되었습니다</h2><ul><li>이미지 다중 업로드 기능</li><li>콘텐츠 검색 기능 개선</li><li>UI/UX 개선</li></ul>', TRUE, 89, 'admin'),

-- 뉴스
(2, '2024년 새해 인사', '<h2>새해 복 많이 받으세요</h2><p>2024년 갑진년 새해가 밝았습니다. 올 한 해도 건강하고 행복한 한 해 되시길 바랍니다.</p>', TRUE, 234, 'admin'),
(2, '월간 통계 보고서 발표', '<h2>2023년 12월 통계</h2><p>방문자 수: 10,000명<br>페이지뷰: 50,000회<br>평균 체류시간: 3분 25초</p>', TRUE, 67, 'editor'),

-- 블로그
(3, 'Spring Boot와 HTMX로 만드는 현대적인 웹 애플리케이션', '<h2>HTMX란?</h2><p>HTMX는 HTML 속성을 사용하여 AJAX, CSS Transitions, WebSockets 등을 쉽게 사용할 수 있게 해주는 라이브러리입니다.</p><h3>주요 특징</h3><ul><li>JavaScript 코드 최소화</li><li>서버 사이드 렌더링 활용</li><li>점진적 향상</li></ul>', TRUE, 456, 'editor'),
(3, 'MyBatis vs JPA: 어떤 것을 선택해야 할까?', '<h2>MyBatis의 장점</h2><p>SQL을 직접 작성하여 세밀한 제어가 가능합니다.</p><h2>JPA의 장점</h2><p>객체 지향적인 개발이 가능하고 생산성이 높습니다.</p>', TRUE, 321, 'editor'),

-- 이벤트
(4, '신규 회원 가입 이벤트', '<h2>지금 가입하고 혜택 받으세요!</h2><p>기간: 2024.01.01 ~ 2024.01.31</p><p>혜택: 신규 가입자 전원 포인트 5,000원 지급</p>', TRUE, 789, 'admin'),
(4, '설날 특별 프로모션', '<h2>설날 맞이 특별 할인</h2><p>모든 상품 최대 50% 할인</p><p>기간: 2024.02.09 ~ 2024.02.12</p>', FALSE, 0, 'admin'),

-- FAQ
(5, '비밀번호를 잊어버렸어요', '<h2>비밀번호 재설정 방법</h2><ol><li>로그인 페이지에서 "비밀번호 찾기" 클릭</li><li>가입시 등록한 이메일 입력</li><li>이메일로 전송된 링크 클릭</li><li>새 비밀번호 설정</li></ol>', TRUE, 543, 'admin'),
(5, '회원 탈퇴는 어떻게 하나요?', '<h2>회원 탈퇴 절차</h2><p>마이페이지 > 설정 > 회원탈퇴 메뉴에서 탈퇴 신청이 가능합니다.</p><p>※ 탈퇴 후 7일간은 데이터가 보관되며, 이후 완전 삭제됩니다.</p>', TRUE, 234, 'admin');

-- ============================================
-- 4. 이미지 샘플 데이터 (선택사항)
-- ============================================
-- 실제 파일이 업로드되어야 하므로 주석 처리
/*
INSERT INTO image (content_id, original_name, saved_name, file_path, file_size, display_order, create_name) VALUES
(1, 'notice_banner.jpg', '20240101_123456_notice_banner.jpg', '/uploads/2024/01/01/20240101_123456_notice_banner.jpg', 245680, 1, 'admin'),
(3, 'htmx_logo.png', '20240102_234567_htmx_logo.png', '/uploads/2024/01/02/20240102_234567_htmx_logo.png', 89234, 1, 'editor'),
(5, 'spring_boot.jpg', '20240103_345678_spring_boot.jpg', '/uploads/2024/01/03/20240103_345678_spring_boot.jpg', 156789, 1, 'editor');
*/
