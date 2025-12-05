package com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity;

public enum LeaveType {
    EARLY_LEAVE("조퇴"),
    ABSENCE("결석");

    private final String displayName;

    LeaveType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}