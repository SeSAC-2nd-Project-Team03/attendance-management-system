package com.sesac2ndproject.attendancemanagementsystem.global.error.exception;

/**
 * 출석 관련 예외의 기본 클래스
 */
public class AttendanceException extends RuntimeException {

    public AttendanceException(String message) {
        super(message);
    }

    public AttendanceException(String message, Throwable cause) {
        super(message, cause);
    }
}

