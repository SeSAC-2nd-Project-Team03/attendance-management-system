package com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DetailedAttendance;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class DailyAttendanceDTO {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestByDateAndCourseIdDTO{
        private LocalDate date;
        private Long courseId;
    }
    // 통합 출석부 조회:** 날짜 + 과정ID를 받으면 → 해당 수강생들의 `DailyAttendance`와 `DetailedAttendance`를 조인(또는 Fetch)하여 가져오는 DTO.
    public static class ResponseByDateAndCourseIdDTO{
        private Long attendanceId;
        private Long memberId;
        private Long courseId;
        private LocalDate date;
        private AttendanceStatus finalStatus;
        private List<DetailedAttendance> detailedAttendanceList;
    }

}
