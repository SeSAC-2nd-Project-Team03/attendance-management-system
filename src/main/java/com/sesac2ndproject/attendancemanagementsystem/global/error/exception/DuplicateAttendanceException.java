package com.sesac2ndproject.attendancemanagementsystem.global.error.exception;

import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;

/**
 * 이미 출석을 완료했을 때 발생하는 예외
 */
public class DuplicateAttendanceException extends AttendanceException {

    public DuplicateAttendanceException() {
        super("이미 출석을 완료하셨습니다.");
    }

    public DuplicateAttendanceException(AttendanceType type) {
        super(String.format("이미 %s 출석을 완료하셨습니다.", type));
    }

    public DuplicateAttendanceException(String message) {
        super(message);
    }
}

