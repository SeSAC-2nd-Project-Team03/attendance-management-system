package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity;

import com.sesac2ndproject.attendancemanagementsystem.global.entity.BaseTimeEntity;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "attendance_config") // DB 테이블명 명시
public class AttendanceConfig extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long courseId; // 어떤 과정(반)의 출석인지 (예: 자바반)

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
    private LocalTime standardTime; // 기준 시간 (예: 09:00, 12:30)

    // 생성자 (관리자가 설정을 생성할 때 사용)
    public AttendanceConfig(Long courseId, Long adminId, AttendanceType type, String authNumber, LocalDate targetDate, LocalTime standardTime) {
        this.courseId = courseId;
        this.adminId = adminId;
        this.type = type;
        this.authNumber = authNumber;
        this.targetDate = targetDate;
        this.standardTime = standardTime;
    }
}
