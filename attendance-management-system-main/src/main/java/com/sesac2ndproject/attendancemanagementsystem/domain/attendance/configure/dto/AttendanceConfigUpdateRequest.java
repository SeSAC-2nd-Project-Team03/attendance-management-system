package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.configure.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class AttendanceConfigUpdateRequest {

    private String authNumber; // 인증번호 변경

    private LocalTime standardTime;

    private Integer validMinutes;
}