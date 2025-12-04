package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity;

import com.sesac2ndproject.attendancemanagementsystem.global.entity.BaseTimeEntity;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 출석 설정 엔티티
 * 관리자가 설정하는 출석 인증번호와 기준시간 정보
 *
 * Person 1은 이 테이블을 READ ONLY로 사용합니다.
 * (DataInitializer 또는 관리자가 설정 데이터를 INSERT/UPDATE)
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder  // ✅ Lombok이 자동으로 builder() 메서드 생성
@Table(
        name = "attendance_config",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_course_date_session",
                        columnNames = {"courseId", "targetDate", "type"}
                )
        }
)
public class AttendanceConfig extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long courseId; // 어떤 과정(반)의 출석인지

    @Column(nullable = false)
    private Long adminId; // 누가 이 번호를 만들었는지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceType type; // MORNING(아침), LUNCH(점심), DINNER(저녁)

    @Column(nullable = false)
    private String authNumber; // 정답 번호 (예: "1234")

    @Column(nullable = false)
    private LocalDate targetDate; // 출석 날짜 (예: 2025-11-28)

    @Column(nullable = false)
    private LocalTime standardTime; // 기준 시간 (예: 08:50, 13:10)

    @Column(nullable = false)
    private LocalTime deadline; // 마감 시간 (예: 09:10, 13:30)

    @Column(nullable = false)
    private Integer validMinutes; // 유효 시간 (분 단위, 예: 20분)
}