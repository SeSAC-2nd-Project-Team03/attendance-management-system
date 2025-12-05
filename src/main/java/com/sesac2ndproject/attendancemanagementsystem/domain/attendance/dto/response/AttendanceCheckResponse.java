package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 출석 체크 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AttendanceCheckResponse {

    private boolean success;
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkTime;

    /**
     * 성공 응답 생성
     */
    public static AttendanceCheckResponse success(String message, LocalDateTime checkTime) {
        return AttendanceCheckResponse.builder()
                .success(true)
                .message(message)
                .checkTime(checkTime)
                .build();
    }

    /**
     * 실패 응답 생성
     */
    public static AttendanceCheckResponse failure(String message, LocalDateTime checkTime) {
        return AttendanceCheckResponse.builder()
                .success(false)
                .message(message)
                .checkTime(checkTime)
                .build();
    }
}