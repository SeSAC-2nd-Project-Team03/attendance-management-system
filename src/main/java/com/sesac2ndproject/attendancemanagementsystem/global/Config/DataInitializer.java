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

        // 2. 학생 계정 생성 (student1, student2)
        createStudentIfAbsent("student1", "김철수", "010-1111-2222");
        createStudentIfAbsent("student2", "이영희", "010-3333-4444");

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

            enrollmentRepository.save(createEnrollment(s1, javaCourse));
            enrollmentRepository.save(createEnrollment(s2, javaCourse));
            System.out.println("수강신청 데이터 초기화 완료");
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

        // ============================================
        // 5. 출석 상세 기록 (기존 코드 - Person 2용)
        // ============================================
        if (detailedAttendanceRepository.count() == 0) {
            LocalDate today = LocalDate.now();

            // 1) 아침 출석 (성공)
            detailedAttendanceRepository.save(DetailedAttendance.builder()
                    .memberId(s1.getId())
                    .courseId(javaCourse.getId())
                    .dailyAttendanceId(null)  // ✅ Person 2가 나중에 업데이트
                    .type(AttendanceType.MORNING)
                    .inputNumber("1234")
                    .checkTime(LocalDateTime.of(today, LocalTime.of(8, 55)))
                    .connectionIp("127.0.0.1")
                    .isVerified(true)
                    .failReason(null)
                    .build());

            // 2) 점심 출석 (지각 - 실패)
            detailedAttendanceRepository.save(DetailedAttendance.builder()
                    .memberId(s1.getId())
                    .courseId(javaCourse.getId())
                    .dailyAttendanceId(null)
                    .type(AttendanceType.LUNCH)
                    .inputNumber("5678")
                    .checkTime(LocalDateTime.of(today, LocalTime.of(13, 35)))  // 마감 후
                    .connectionIp("127.0.0.1")
                    .isVerified(false)
                    .failReason("출석 가능 시간이 아닙니다. (출석 가능: 13:10 ~ 13:30)")
                    .build());

            // 3) 저녁 출석 (데이터 없음 - 퇴근 안 찍음)
            // 일부러 안 넣음 -> Person 2가 이걸 보고 '결석/조퇴' 처리하는 로직을 테스트

            System.out.println("✅ [Person 2용] 출석 상세 더미 데이터 생성 완료 (아침:O, 점심:지각, 저녁:X)");
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