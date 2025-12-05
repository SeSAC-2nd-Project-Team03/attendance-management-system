package com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity;

public enum LeaveStatus {
    PENDING("대기"),
    APPROVED("승인"),
    REJECTED("거절"),
    CANCELLED("취소");

    private final String displayName;

    LeaveStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}