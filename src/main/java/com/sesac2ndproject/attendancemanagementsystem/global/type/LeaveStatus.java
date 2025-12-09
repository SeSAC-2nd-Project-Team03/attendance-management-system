package com.sesac2ndproject.attendancemanagementsystem.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LeaveStatus {
    PENDING("대기중"),   // 신청 직후
    APPROVED("승인됨"),  // 관리자가 승인함
    REJECTED("반려됨"),  // 관리자가 거절함
    CANCELLED("취소됨"); // 사용자가 취소함

    private final String description;
}
