package com.sesac2ndproject.attendancemanagementsystem.global.Config;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.AttendanceConfig;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DailyAttendance;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DetailedAttendance;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.AttendanceConfigRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.DailyAttendanceRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.DetailedAttendanceRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.course.entity.Course;
import com.sesac2ndproject.attendancemanagementsystem.domain.course.entity.Enrollment;
import com.sesac2ndproject.attendancemanagementsystem.domain.course.repository.CourseRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.course.repository.EnrollmentRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.member.entity.Member;
import com.sesac2ndproject.attendancemanagementsystem.domain.member.repository.MemberRepository;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceStatus;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import com.sesac2ndproject.attendancemanagementsystem.global.type.EnrollmentStatus;
import com.sesac2ndproject.attendancemanagementsystem.global.type.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;


@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final DetailedAttendanceRepository detailedAttendanceRepository;
    private final AttendanceConfigRepository attendanceConfigRepository;
    private final DailyAttendanceRepository dailyAttendanceRepository;

    @Override
    public void run(String... args) throws Exception {

        // 1. 관리자 계정 생성
        if (memberRepository.findByLoginId("admin").isEmpty()) {
            Member admin = Member.builder()
                    .loginId("admin")
                    .password(passwordEncoder.encode("1234"))
                    .name("관리자")
                    .role(RoleType.ADMIN)
                    .build();
            memberRepository.save(admin);
            System.out.println("관리자 계정 생성 완료");
        }

        // 2. 학생 계정 생성 (student1, student2, student3)
        createStudentIfAbsent("student1", "김철수", "010-1111-2222");
        createStudentIfAbsent("student2", "이영희", "010-3333-4444");
        createStudentIfAbsent("student3", "박조퇴", "010-5555-6666");

        // 3. 과정(Course) 생성 및 가져오기
        Course javaCourse;
        if (courseRepository.count() == 0) {
            javaCourse = Course.builder()
                    .courseName("자바 백엔드 개발자 양성과정")
                    .description("Spring Boot 중심의 백엔드 과정")
                    .startDate(LocalDate.of(2025, 11, 1))
                    .endDate(LocalDate.of(2026, 5, 1))
                    .build();
            courseRepository.save(javaCourse);
            System.out.println("과정(Course) 데이터 생성 완료");
        } else {
            javaCourse = courseRepository.findAll().get(0);
        }

        // 4. 수강신청 (학생과 과정이 있을 때만 진행)
        if (enrollmentRepository.count() == 0) {
            Member s1 = memberRepository.findByLoginId("student1").orElseThrow();
            Member s2 = memberRepository.findByLoginId("student2").orElseThrow();
            Member s3 = memberRepository.findByLoginId("student3").orElseThrow();

            enrollmentRepository.save(createEnrollment(s1, javaCourse));
            enrollmentRepository.save(createEnrollment(s2, javaCourse));
            enrollmentRepository.save(createEnrollment(s3, javaCourse));
            System.out.println("수강신청 데이터 초기화 완료");
        }

        // 5. 과거 데이터 대량 생성 (어제부터 5일 전까지)
        // 목표: 10개 이상의 DailyAttendance 데이터 만들기
        // =====================================================================
        if (dailyAttendanceRepository.count() < 5) { // 데이터가 너무 적으면 실행
            System.out.println("🔄 [테스트용] 과거 5일치 출석 데이터 생성 시작...");
            Member s1 = memberRepository.findByLoginId("student1").orElseThrow();
            Member s2 = memberRepository.findByLoginId("student2").orElseThrow();
            Member s3 = memberRepository.findByLoginId("student3").orElseThrow();

            List<Member> students = Arrays.asList(s1, s2, s3);
            LocalDate yesterday = LocalDate.now().minusDays(1);

            // 어제부터 과거 5일간 반복 (총 3명 * 5일 = 15개 Daily 데이터 생성)
            for (int i = 0; i < 5; i++) {
                LocalDate targetDate = yesterday.minusDays(i);

                for (Member student : students) {
                    createPastData(student, javaCourse, targetDate);
                }
            }
            System.out.println("✅ [테스트용] 과거 데이터 생성 완료 (Daily 15개 추가됨)");
        }

        // ============================================
        // ✅ Person 1: 출석 설정(AttendanceConfig) 생성
        // ============================================
        if (attendanceConfigRepository.count() == 0) {
            LocalDate today = LocalDate.now();
            Member admin = memberRepository.findByLoginId("admin").orElseThrow();

            // 아침 출석 설정 (08:50~09:10, 인증번호: 1234)
            attendanceConfigRepository.save(AttendanceConfig.builder()
                    .courseId(javaCourse.getId())
                    .adminId(admin.getId())  // ✅ 추가
                    .targetDate(today)
                    .type(AttendanceType.MORNING)
                    .authNumber("1234")
                    .standardTime(LocalTime.of(8, 50))  // ✅ 추가
                    .deadline(LocalTime.of(9, 10))
                    .validMinutes(20)
                    .build());

            // 점심 출석 설정 (13:10~13:30, 인증번호: 5678)
            attendanceConfigRepository.save(AttendanceConfig.builder()
                    .courseId(javaCourse.getId())
                    .adminId(admin.getId())  // ✅ 추가
                    .targetDate(today)
                    .type(AttendanceType.LUNCH)
                    .authNumber("5678")
                    .standardTime(LocalTime.of(13, 10))  // ✅ 추가
                    .deadline(LocalTime.of(13, 30))
                    .validMinutes(20)
                    .build());

            // 저녁 출석 설정 (17:50~18:10, 인증번호: 9999)
            attendanceConfigRepository.save(AttendanceConfig.builder()
                    .courseId(javaCourse.getId())
                    .adminId(admin.getId())  // ✅ 추가
                    .targetDate(today)
                    .type(AttendanceType.DINNER)
                    .authNumber("9999")
                    .standardTime(LocalTime.of(17, 50))  // ✅ 추가
                    .deadline(LocalTime.of(18, 10))
                    .validMinutes(20)
                    .build());

            System.out.println("✅ [Person 1] 출석 설정(AttendanceConfig) 생성 완료");
        }

        Member s1 = memberRepository.findByLoginId("student1").orElseThrow();
        Member s2 = memberRepository.findByLoginId("student2").orElseThrow();
        Member s3 = memberRepository.findByLoginId("student3").orElseThrow();

        // ============================================
        // 5. 출석 상세 기록 (Person 2용)
        // ============================================
        if (detailedAttendanceRepository.count() == 0) {
            LocalDate today = LocalDate.now();

            // ============================================
            // 🔵 케이스 1: 모두 출석 (student1)
            // 아침(O) + 점심(O) + 저녁(O) → PRESENT
            // ============================================
            
            // 1) 아침 출석 (정시)
            detailedAttendanceRepository.save(DetailedAttendance.builder()
                    .memberId(s1.getId())
                    .courseId(javaCourse.getId())
                    .dailyAttendanceId(null)
                    .type(AttendanceType.MORNING)
                    .inputNumber("1234")
                    .checkTime(LocalDateTime.of(today, LocalTime.of(8, 55)))  // 08:55 (정시)
                    .connectionIp("192.168.1.100")
                    .isVerified(true)
                    .failReason(null)
                    .build());

            // 2) 점심 출석 (정시)
            detailedAttendanceRepository.save(DetailedAttendance.builder()
                    .memberId(s1.getId())
                    .courseId(javaCourse.getId())
                    .dailyAttendanceId(null)
                    .type(AttendanceType.LUNCH)
                    .inputNumber("5678")
                    .checkTime(LocalDateTime.of(today, LocalTime.of(13, 15)))  // 13:15 (정시)
                    .connectionIp("192.168.1.100")
                    .isVerified(true)
                    .failReason(null)
                    .build());

            // 3) 저녁 출석 (정시)
            detailedAttendanceRepository.save(DetailedAttendance.builder()
                    .memberId(s1.getId())
                    .courseId(javaCourse.getId())
                    .dailyAttendanceId(null)
                    .type(AttendanceType.DINNER)
                    .inputNumber("9999")
                    .checkTime(LocalDateTime.of(today, LocalTime.of(17, 55)))  // 17:55 (정시)
                    .connectionIp("192.168.1.100")
                    .isVerified(true)
                    .failReason(null)
                    .build());

            System.out.println("🔵 [student1] 모두 출석: 아침(O) + 점심(O) + 저녁(O) → PRESENT");

            // ============================================
            // 🟡 케이스 2: 지각 (student2)
            // 아침(X) + 점심(O) + 저녁(O) → LATE
            // ============================================
            
            // 1) 아침 결석 (시간 초과)
            detailedAttendanceRepository.save(DetailedAttendance.builder()
                    .memberId(s2.getId())
                    .courseId(javaCourse.getId())
                    .dailyAttendanceId(null)
                    .type(AttendanceType.MORNING)
                    .inputNumber("1234")
                    .checkTime(LocalDateTime.of(today, LocalTime.of(9, 30)))  // 09:30 (마감 후)
                    .connectionIp("192.168.1.101")
                    .isVerified(false)
                    .failReason("출석 가능 시간이 아닙니다. (출석 가능: 08:50 ~ 09:10)")
                    .build());

            // 2) 점심 출석 (정시)
            detailedAttendanceRepository.save(DetailedAttendance.builder()
                    .memberId(s2.getId())
                    .courseId(javaCourse.getId())
                    .dailyAttendanceId(null)
                    .type(AttendanceType.LUNCH)
                    .inputNumber("5678")
                    .checkTime(LocalDateTime.of(today, LocalTime.of(13, 20)))  // 13:20 (정시)
                    .connectionIp("192.168.1.101")
                    .isVerified(true)
                    .failReason(null)
                    .build());

            // 3) 저녁 출석 (정시)
            detailedAttendanceRepository.save(DetailedAttendance.builder()
                    .memberId(s2.getId())
                    .courseId(javaCourse.getId())
                    .dailyAttendanceId(null)
                    .type(AttendanceType.DINNER)
                    .inputNumber("9999")
                    .checkTime(LocalDateTime.of(today, LocalTime.of(18, 0)))  // 18:00 (정시)
                    .connectionIp("192.168.1.101")
                    .isVerified(true)
                    .failReason(null)
                    .build());

            System.out.println("🟡 [student2] 지각: 아침(X) + 점심(O) + 저녁(O) → LATE");

            // ============================================
            // 🟠 케이스 3: 조퇴 (student3)
            // 아침(O) + 점심(O) + 저녁(X) → LEAVE
            // ============================================
            
            // 1) 아침 출석 (정시)
            detailedAttendanceRepository.save(DetailedAttendance.builder()
                    .memberId(s3.getId())
                    .courseId(javaCourse.getId())
                    .dailyAttendanceId(null)
                    .type(AttendanceType.MORNING)
                    .inputNumber("1234")
                    .checkTime(LocalDateTime.of(today, LocalTime.of(8, 58)))  // 08:58 (정시)
                    .connectionIp("192.168.1.102")
                    .isVerified(true)
                    .failReason(null)
                    .build());

            // 2) 점심 출석 (정시)
            detailedAttendanceRepository.save(DetailedAttendance.builder()
                    .memberId(s3.getId())
                    .courseId(javaCourse.getId())
                    .dailyAttendanceId(null)
                    .type(AttendanceType.LUNCH)
                    .inputNumber("5678")
                    .checkTime(LocalDateTime.of(today, LocalTime.of(13, 12)))  // 13:12 (정시)
                    .connectionIp("192.168.1.102")
                    .isVerified(true)
                    .failReason(null)
                    .build());

            // 3) 저녁 출석 없음 (조퇴) - 아예 기록 안 남김!

            System.out.println("🟠 [student3] 조퇴: 아침(O) + 점심(O) + 저녁(X) → LEAVE");
        }
    }
    /* [ 헬퍼 메서드 ] */
    private void createPastData(Member student, Course course, LocalDate date) {
        // 학생별/날짜별 랜덤 시나리오
        AttendanceStatus status;
        boolean forceLate = false;
        boolean forceAbsent = false;

        if (student.getLoginId().equals("student1")) {
            status = AttendanceStatus.PRESENT; // 김철수: 개근
        } else if (student.getLoginId().equals("student2")) {
            // 이영희: 짝수 날짜 지각
            forceLate = (date.getDayOfMonth() % 2 == 0);
            status = forceLate ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;
        } else {
            // 박조퇴: 3의 배수 날짜 결석
            forceAbsent = (date.getDayOfMonth() % 3 == 0);
            status = forceAbsent ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT;
        }

        // 1. DailyAttendance 저장 (ID 생성을 위해 먼저 저장)
        DailyAttendance daily = DailyAttendance.builder()
                .memberId(student.getId())
                .courseId(course.getId())
                .date(date)
                .status(status)
                .morningStatus(forceAbsent ? AttendanceStatus.ABSENT : (forceLate ? AttendanceStatus.LATE : AttendanceStatus.PRESENT))
                .lunchStatus(forceAbsent ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT)
                .dinnerStatus(forceAbsent ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT)
                .build();

        DailyAttendance savedDaily = dailyAttendanceRepository.save(daily);

        // 2. DetailedAttendance 저장 (Daily ID 연결)
        if (!forceAbsent) {
            // 아침 (지각이면 09:30, 아니면 08:50)
            createDetail(student, course, savedDaily.getId(), AttendanceType.MORNING, date,
                    forceLate ? LocalTime.of(9, 30) : LocalTime.of(8, 50), !forceLate);
            // 점심 (13:10)
            createDetail(student, course, savedDaily.getId(), AttendanceType.LUNCH, date,
                    LocalTime.of(13, 10), true);
            // 저녁 (18:00)
            createDetail(student, course, savedDaily.getId(), AttendanceType.DINNER, date,
                    LocalTime.of(18, 0), true);
        }
    }
    private void createDetail(Member m, Course c, Long dailyId, AttendanceType type, LocalDate date, LocalTime time, boolean verified) {
        detailedAttendanceRepository.save(DetailedAttendance.builder()
                .memberId(m.getId())
                .courseId(c.getId())
                .dailyAttendanceId(dailyId) // ✅ 연결!
                .type(type)
                .inputNumber("1234")
                .checkTime(LocalDateTime.of(date, time))
                .connectionIp("127.0.0.1")
                .isVerified(verified)
                .failReason(verified ? null : "지각 또는 인증 실패")
                .build());
    }

    // 학생 생성 헬퍼 메서드
    private void createStudentIfAbsent(String loginId, String name, String phone) {
        if (memberRepository.findByLoginId(loginId).isEmpty()) {
            Member student = Member.builder()
                    .loginId(loginId)
                    .password(passwordEncoder.encode("1234"))
                    .name(name)
                    .phoneNumber(phone)
                    .role(RoleType.USER)
                    .build();
            memberRepository.save(student);
            System.out.println("학생 계정(" + loginId + ") 생성 완료");
        }
    }

    // 수강신청 생성 헬퍼 메서드
    private Enrollment createEnrollment(Member member, Course course) {
        return Enrollment.builder()
                .member(member)
                .course(course)
                .status(EnrollmentStatus.ACTIVE)
                .statusChangedAt(LocalDateTime.now())
                .build();
    }
}