package com.sesac2ndproject.attendancemanagementsystem.global.error.exception;

/**
 * 인증번호가 올바르지 않을 때 발생하는 예외
 */
public class InvalidAuthNumberException extends AttendanceException {

    public InvalidAuthNumberException() {
        super("인증번호가 올바르지 않습니다.");
    }

    public InvalidAuthNumberException(String message) {
        super(message);
    }
}

