package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity;

import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceStatus;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "daily_attendance")
public class DailyAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dailyAttendanceId;
    private Long memberId;
    private LocalDate date;
    private AttendanceStatus finalStatus;
}
