package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.query.dto;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.common.entity.DetailedAttendance;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAttendanceFlatDTO {
    private Long dailyAttendanceId;
    private Long memberId;
    private String memberName;
    private Long courseId;
    private String courseName;
    private LocalDate workDate;
    private AttendanceStatus totalStatus;

    // 리스트가 아니라 '단일 객체'로 받습니다.
    private DetailedAttendance detailedAttendance;
}
