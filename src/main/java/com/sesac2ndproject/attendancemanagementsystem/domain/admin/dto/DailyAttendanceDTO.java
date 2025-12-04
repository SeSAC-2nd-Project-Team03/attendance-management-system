package com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class DailyAttendanceDTO {
    private LocalDate date;
    private Long courseId;
}
