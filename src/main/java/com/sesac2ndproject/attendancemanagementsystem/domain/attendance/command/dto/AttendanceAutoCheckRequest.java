package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.command.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 자동 출석 체크 요청 DTO
 * 시간 기반으로 출석 타입을 자동 판단합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceAutoCheckRequest {

    @NotNull(message = "회원 ID는 필수입니다.")
    private Long memberId;

    @NotNull(message = "강의 ID는 필수입니다.")
    private Long courseId;

    @NotBlank(message = "인증번호는 필수입니다.")
    private String inputNumber;
}

