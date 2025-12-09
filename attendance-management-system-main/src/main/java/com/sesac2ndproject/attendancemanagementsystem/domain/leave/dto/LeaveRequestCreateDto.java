package com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity.LeaveRequest;
import com.sesac2ndproject.attendancemanagementsystem.domain.member.entity.Member;
import com.sesac2ndproject.attendancemanagementsystem.global.type.LeaveStatus;
import com.sesac2ndproject.attendancemanagementsystem.global.type.LeaveType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class LeaveRequestCreateDto {

    // 휴가 종류
    private LeaveType leaveType;

    // 시작일
    private LocalDate startDate;

    // 종료일 (당일 연차라면 startDate와 같은 값)
    private LocalDate endDate;

    // 사유
    private String reason;

}