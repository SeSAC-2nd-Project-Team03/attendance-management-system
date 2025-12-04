package com.sesac2ndproject.attendancemanagementsystem.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 정의
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 오류가 발생했습니다."),

    // 출석 관련 에러
    INVALID_AUTH_NUMBER(HttpStatus.BAD_REQUEST, "A001", "인증번호가 올바르지 않습니다."),
    ATTENDANCE_TIME_EXPIRED(HttpStatus.BAD_REQUEST, "A002", "출석 가능 시간이 아닙니다."),
    DUPLICATE_ATTENDANCE(HttpStatus.CONFLICT, "A003", "이미 출석을 완료하셨습니다."),
    ATTENDANCE_CONFIG_NOT_FOUND(HttpStatus.NOT_FOUND, "A004", "출석 설정이 존재하지 않습니다."),
    INVALID_IP_ADDRESS(HttpStatus.FORBIDDEN, "A005", "허용되지 않은 IP 주소입니다."),

    // 인증 관련 에러
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH001", "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH002", "접근 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
