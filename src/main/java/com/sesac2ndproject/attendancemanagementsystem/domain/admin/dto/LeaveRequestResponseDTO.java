package com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto;

import com.sesac2ndproject.attendancemanagementsystem.global.type.LeaveStatus;
import com.sesac2ndproject.attendancemanagementsystem.global.type.LeaveType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequestResponseDTO {

    private Long id;            // 신청서 ID
    private Long memberId;      // 신청자 ID
    private String memberName;  // 신청자 이름
    private LocalDate targetDate;   // 조퇴/결석 날짜
    private LeaveType type;         // 조퇴/결석인지
    private LeaveStatus status;     // 현재 상태
    private String reason;          // 사유



}
