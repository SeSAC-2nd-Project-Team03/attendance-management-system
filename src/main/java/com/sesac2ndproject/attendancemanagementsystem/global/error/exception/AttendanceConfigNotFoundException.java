package com.sesac2ndproject.attendancemanagementsystem.global.error.exception;

import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;

/**
 * 출석 설정이 존재하지 않을 때 발생하는 예외
 */
public class AttendanceConfigNotFoundException extends AttendanceException {

    public AttendanceConfigNotFoundException() {
        super("출석 설정이 존재하지 않습니다.");
    }

    public AttendanceConfigNotFoundException(AttendanceType type) {
        super(String.format("오늘 %s 출석 설정이 존재하지 않습니다.", type));
    }

    public AttendanceConfigNotFoundException(String message) {
        super(message);
    }
}

