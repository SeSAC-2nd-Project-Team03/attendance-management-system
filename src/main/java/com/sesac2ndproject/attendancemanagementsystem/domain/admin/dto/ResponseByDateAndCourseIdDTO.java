package com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DetailedAttendance;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
// 통합 출석부 조회:** 날짜 + 과정ID를 받으면 → 해당 수강생들의 `DailyAttendance`와 `DetailedAttendance`를 조인(또는 Fetch)하여 가져오는 DTO.
public class ResponseByDateAndCourseIdDTO {

    // 1. DailyAttendance 관련 정보
    private Long dailyAttendanceId;
    private Long memberId;
    private Long courseId;
    private LocalDate date;
    private AttendanceStatus status;

    // 2. DetailedAttendance (상세 기록) - 객체 통째로 받기
    // LEFT JOIN이므로 이 값은 null일 수도 있음.
    private DetailedAttendance detailedAttendance;
}
