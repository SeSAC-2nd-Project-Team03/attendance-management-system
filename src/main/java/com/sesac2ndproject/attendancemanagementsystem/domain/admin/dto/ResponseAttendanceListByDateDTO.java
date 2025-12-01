package com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto;

import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceStatus;
import com.sesac2ndproject.attendancemanagementsystem.global.type.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ResponseAttendanceListByDateDTO {
    private Long id;
    private Long memberId;
    private LocalDate date;
    private List<AttendanceStatus> attendanceStatus;
    private Long memberName;
    private String courseName;
    private List<EnrollmentStatus> enrollmentStatus;
}
