package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DetailedAttendance;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 출석 상세 기록 응답
 */
@Getter
@Builder
public class AttendanceDetailResponse {

    private Long id;
    private AttendanceType type;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkTime;

    private boolean verified;
    private String failReason;

    // Entity -> DTO 변환
    public static AttendanceDetailResponse from(DetailedAttendance entity) {
        return AttendanceDetailResponse.builder()
                .id(entity.getId())
                .type(entity.getType())
                .checkTime(entity.getCheckTime())
                .verified(entity.isVerified())
                .failReason(entity.getFailReason())
                .build();
    }
}

