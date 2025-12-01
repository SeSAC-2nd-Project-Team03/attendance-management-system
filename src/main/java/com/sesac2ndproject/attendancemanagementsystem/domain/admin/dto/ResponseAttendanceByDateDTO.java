package com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto;

import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceStatus;
import com.sesac2ndproject.attendancemanagementsystem.global.type.EnrollmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ResponseAttendanceByDateDTO {
    private Long id;
    private Long memberId;
    private LocalDate date;
    private AttendanceStatus attendanceStatus;
    private Long memberName;
    private String courseName;
    private EnrollmentStatus enrollmentStatus;
}
