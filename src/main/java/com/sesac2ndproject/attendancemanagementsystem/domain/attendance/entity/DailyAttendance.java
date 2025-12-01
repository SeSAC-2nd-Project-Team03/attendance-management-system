package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity;

import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import jakarta.persistence.*; // ⭐️ 중요: javax가 아니라 jakarta여야 함
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity // 👈 이 녀석이 범인일 확률 99%
@Getter
@NoArgsConstructor
public class DailyAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long member_id;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private AttendanceType status;
}
