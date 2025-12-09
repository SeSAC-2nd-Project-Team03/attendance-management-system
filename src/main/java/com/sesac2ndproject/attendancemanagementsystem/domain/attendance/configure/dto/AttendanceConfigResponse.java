package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.configure.dto;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.configure.entity.AttendanceConfig;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class AttendanceConfigResponse {

    private Long id;
    private Long courseId;
    private Long adminId;
    private AttendanceType type;
    private String authNumber;
    private LocalDate targetDate;
    private LocalTime standardTime;
    private LocalTime deadline;
    private Integer validMinutes;

    public static AttendanceConfigResponse from(AttendanceConfig entity) {
        return AttendanceConfigResponse.builder()
                .id(entity.getId())
                .courseId(entity.getCourseId())
                .adminId(entity.getAdminId())
                .type(entity.getType())
                .authNumber(entity.getAuthNumber())
                .targetDate(entity.getTargetDate())
                .standardTime(entity.getStandardTime())
                .deadline(entity.getDeadline())
                .validMinutes(entity.getValidMinutes())
                .build();
    }
}
