package com.sesac2ndproject.attendancemanagementsystem.global.error.exception;

import java.time.LocalTime;

/**
 * 출석 가능 시간이 아닐 때 발생하는 예외
 */
public class AttendanceTimeExpiredException extends AttendanceException {

    public AttendanceTimeExpiredException() {
        super("출석 가능 시간이 아닙니다.");
    }

    public AttendanceTimeExpiredException(LocalTime startTime, LocalTime endTime) {
        super(String.format("출석 가능 시간이 아닙니다. (출석 가능: %s ~ %s)", startTime, endTime));
    }

    public AttendanceTimeExpiredException(String message) {
        super(message);
    }
}

