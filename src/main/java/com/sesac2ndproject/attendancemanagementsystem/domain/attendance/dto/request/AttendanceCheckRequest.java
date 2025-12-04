package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.request;

import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 출석 체크 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCheckRequest {

    @NotNull(message = "회원 ID는 필수입니다.")
    private Long memberId;  // ✅ 추가: SecurityUtil 대신 직접 받기

    @NotNull(message = "강의 ID는 필수입니다.")
    private Long courseId;

    @NotNull(message = "출석 타입은 필수입니다.")
    private AttendanceType type;

    @NotBlank(message = "인증번호는 필수입니다.")
    private String inputNumber;
}