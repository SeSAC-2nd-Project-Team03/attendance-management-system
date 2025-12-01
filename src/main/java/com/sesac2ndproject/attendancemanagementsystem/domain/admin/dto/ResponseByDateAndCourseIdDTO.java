package com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DetailedAttendance;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceStatus;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
// 통합 출석부 조회:** 날짜 + 과정ID를 받으면 → 해당 수강생들의 `DailyAttendance`와 `DetailedAttendance`를 조인(또는 Fetch)하여 가져오는 DTO.
public class ResponseByDateAndCourseIdDTO {
    private Long id;
    private Long memberId;
    private Long courseId;
    private LocalDate date;
    private AttendanceStatus status;
    private DetailedAttendance detailedAttendanceList;
    private Long dailyAttendanceId;
    private AttendanceType type; // 아침/점심/저녁 중 무엇인지
    private String inputNumber; // 학생이 입력한 번호 ("1234")
    private LocalDateTime checkTime; // 실제 찍은 시간 (2025-11-28 09:05:12)
    private String connectionIp; // 접속 IP (검증용)
    private boolean isVerified;
}
