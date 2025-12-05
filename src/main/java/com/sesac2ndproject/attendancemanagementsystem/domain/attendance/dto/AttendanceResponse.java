package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto;

public class AttendanceResponse {
    private String message;

    public AttendanceResponse(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
}
