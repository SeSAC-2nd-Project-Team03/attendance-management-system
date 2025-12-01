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
}
