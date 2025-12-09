package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.common.dto.response;

import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyAttendanceResponse {
    private Long id;
    private Long memberId;
    private Long courseId;
    private LocalDate date;
    private AttendanceStatus morningStatus;
    private AttendanceStatus lunchStatus;
    private AttendanceStatus dinnerStatus;
    private AttendanceStatus status;


}
