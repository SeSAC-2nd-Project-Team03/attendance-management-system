package com.sesac2ndproject.attendancemanagementsystem.global.Config;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.AttendanceConfig;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DetailedAttendance;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.AttendanceConfigRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.DetailedAttendanceRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.course.entity.Course;
import com.sesac2ndproject.attendancemanagementsystem.domain.course.entity.Enrollment;
import com.sesac2ndproject.attendancemanagementsystem.domain.course.repository.CourseRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.course.repository.EnrollmentRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.member.entity.Member;
import com.sesac2ndproject.attendancemanagementsystem.domain.member.repository.MemberRepository;
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


@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final DetailedAttendanceRepository detailedAttendanceRepository;
    private final AttendanceConfigRepository attendanceConfigRepository;

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

        // 2. 학생 계정 생성 (더미 데이터)
        // 기존 학생 3명
        createStudentIfAbsent("student1", "김철수", "010-1111-2222");
        createStudentIfAbsent("student2", "이영희", "010-3333-4444");
        createStudentIfAbsent("student3", "박조퇴", "010-5555-6666");
        
        // 추가 학생 더미 데이터 (10명)
        createStudentIfAbsent("student4", "최민수", "010-7777-8888");
        createStudentIfAbsent("student5", "정수진", "010-9999-0000");
        createStudentIfAbsent("student6", "한지훈", "010-1111-3333");
        createStudentIfAbsent("student7", "윤서연", "010-2222-4444");
        createStudentIfAbsent("student8", "오동현", "010-3333-5555");
        createStudentIfAbsent("student9", "강미영", "010-4444-6666");
        createStudentIfAbsent("student10", "임태준", "010-5555-7777");
        createStudentIfAbsent("student11", "배혜진", "010-6666-8888");
        createStudentIfAbsent("student12", "신우진", "010-7777-9999");
        createStudentIfAbsent("student13", "조은서", "010-8888-1111");

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
            // 기존 학생 3명
            Member s1 = memberRepository.findByLoginId("student1").orElseThrow();
            Member s2 = memberRepository.findByLoginId("student2").orElseThrow();
            Member s3 = memberRepository.findByLoginId("student3").orElseThrow();
            
            // 추가 학생 10명
            Member s4 = memberRepository.findByLoginId("student4").orElseThrow();
            Member s5 = memberRepository.findByLoginId("student5").orElseThrow();
            Member s6 = memberRepository.findByLoginId("student6").orElseThrow();
            Member s7 = memberRepository.findByLoginId("student7").orElseThrow();
            Member s8 = memberRepository.findByLoginId("student8").orElseThrow();
            Member s9 = memberRepository.findByLoginId("student9").orElseThrow();
            Member s10 = memberRepository.findByLoginId("student10").orElseThrow();
            Member s11 = memberRepository.findByLoginId("student11").orElseThrow();
            Member s12 = memberRepository.findByLoginId("student12").orElseThrow();
            Member s13 = memberRepository.findByLoginId("student13").orElseThrow();

            // 모든 학생을 과정에 등록
            enrollmentRepository.save(createEnrollment(s1, javaCourse));
            enrollmentRepository.save(createEnrollment(s2, javaCourse));
            enrollmentRepository.save(createEnrollment(s3, javaCourse));
            enrollmentRepository.save(createEnrollment(s4, javaCourse));
            enrollmentRepository.save(createEnrollment(s5, javaCourse));
            enrollmentRepository.save(createEnrollment(s6, javaCourse));
            enrollmentRepository.save(createEnrollment(s7, javaCourse));
            enrollmentRepository.save(createEnrollment(s8, javaCourse));
            enrollmentRepository.save(createEnrollment(s9, javaCourse));
            enrollmentRepository.save(createEnrollment(s10, javaCourse));
            enrollmentRepository.save(createEnrollment(s11, javaCourse));
            enrollmentRepository.save(createEnrollment(s12, javaCourse));
            enrollmentRepository.save(createEnrollment(s13, javaCourse));
            
            System.out.println("수강신청 데이터 초기화 완료 (총 13명의 학생 등록)");
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