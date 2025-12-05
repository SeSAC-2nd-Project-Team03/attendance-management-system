package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto;

import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class AttendanceRequest {
    private Long memberId;      // 회원 ID (기존 userId 대신 사용)
    private Long courseId;      // 강의 ID (서비스에서 필요함)
    private AttendanceType type; // 출석 타입 (MORNING, LUNCH, DINNER)
    private String inputNumber; // 사용자가 입력한 인증 번호
}