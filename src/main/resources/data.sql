-- 1. 회원 (Member) 생성
-- 비밀번호는 테스트용으로 평문 '1234' 사용 (실제론 암호화 필요)
-- 관리자 (ID: 1)
INSERT INTO member (login_id, password, name, role, created_at, updated_at)
VALUES ('admin', '1234', '관리자', 'ROLE_ADMIN', now(), now());

-- 학생 1 (ID: 2) - 김철수 (성실한 학생)
INSERT INTO member (login_id, password, name, phone_number, address, role, created_at, updated_at)
VALUES ('student1', '1234', '김철수', '010-1111-2222', '서울시 강남구', 'ROLE_USER', now(), now());

-- 학생 2 (ID: 3) - 이영희 (지각생)
INSERT INTO member (login_id, password, name, phone_number, address, role, created_at, updated_at)
VALUES ('student2', '1234', '이영희', '010-3333-4444', '경기도 성남시', 'ROLE_USER', now(), now());


-- 2. 과정 (Course) 생성
-- ID: 101 - 자바 백엔드 과정
INSERT INTO course (id, course_name, description, start_date, end_date, created_at, updated_at)
VALUES (101, '자바 백엔드 개발자 양성과정', 'Spring Boot 중심의 백엔드 과정', '2025-11-01', '2026-05-01', now(), now());


-- 3. 수강 신청 (Enrollment) 연결
-- 김철수(2) -> 자바반(101) 수강중
INSERT INTO enrollment (member_id, course_id, status, created_at, updated_at)
VALUES (2, 101, 'ACTIVE', now(), now());

-- 이영희(3) -> 자바반(101) 수강중
INSERT INTO enrollment (member_id, course_id, status, created_at, updated_at)
VALUES (3, 101, 'ACTIVE', now(), now());


-- 4. [Team B 기준 데이터] 출석 설정 (AttendanceConfig)
-- 관리자(1)가 자바반(101)의 2025-11-28일 설정을 미리 생성함
-- 아침 (09:00 기준, 인증번호 1234)
INSERT INTO attendance_config (course_id, admin_id, type, auth_number, target_date, standard_time, created_at, updated_at)
VALUES (101, 1, 'MORNING', '1234', '2025-11-28', '09:00:00', now(), now());

-- 점심 (13:00 기준, 인증번호 5678)
INSERT INTO attendance_config (course_id, admin_id, type, auth_number, target_date, standard_time, created_at, updated_at)
VALUES (101, 1, 'LUNCH', '5678', '2025-11-28', '13:00:00', now(), now());

-- 저녁 (18:00 기준, 인증번호 9999)
INSERT INTO attendance_config (course_id, admin_id, type, auth_number, target_date, standard_time, created_at, updated_at)
VALUES (101, 1, 'DINNER', '9999', '2025-11-28', '18:00:00', now(), now());


-- 5. [Team B & D 데이터] 일일 출석부 (DailyAttendance) 생성
-- 김철수(2)의 11월 28일 출석부 (아직 하루가 안 끝나서 상태는 NONE으로 가정)
INSERT INTO daily_attendance (member_id, date, status, created_at, updated_at)
VALUES (2, '2025-11-28', 'NONE', now(), now());

-- 이영희(3)의 11월 28일 출석부
INSERT INTO daily_attendance (member_id, date, status, created_at, updated_at)
VALUES (3, '2025-11-28', 'NONE', now(), now());


-- 6. [Team B & D 데이터] 상세 출석 로그 (DetailedAttendance) 생성
-- 시나리오: 김철수는 아침에 제때 와서 출석 성공함
-- daily_attendance_id=1 (김철수 출석부), member_id=2
INSERT INTO detailed_attendance (daily_attendance_id, member_id, type, input_number, check_time, connection_ip, is_verified, created_at, updated_at)
VALUES (1, 2, 'MORNING', '1234', '2025-11-28 08:55:00', '192.168.0.1', true, now(), now());

-- 시나리오: 이영희는 아침에 늦잠자서 지각함 (9시 25분에 찍음 -> is_verified가 false거나 로직에 따라 처리)
-- 여기서는 일단 '검증 실패(시간초과)'로 false 처리 예시
INSERT INTO detailed_attendance (daily_attendance_id, member_id, type, input_number, check_time, connection_ip, is_verified, created_at, updated_at)
VALUES (2, 3, 'MORNING', '1234', '2025-11-28 09:25:00', '192.168.0.5', false, now(), now());