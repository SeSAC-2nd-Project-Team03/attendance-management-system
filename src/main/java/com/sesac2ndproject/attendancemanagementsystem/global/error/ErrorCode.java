package com.sesac2ndproject.attendancemanagementsystem.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common (공통 에러 - 코드를 C00x 형태로 체계화하거나, 필요 시 Enum 이름을 그대로 사용 가능)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C003", "지원하지 않는 HTTP 메서드입니다."),

    // Auth & Security (인증/인가 - 핸들러에서 사용하는 UNAUTHORIZED, FORBIDDEN 연결)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH001", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH002", "접근 권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH003", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH004", "만료된 토큰입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH005", "비밀번호가 일치하지 않습니다."),

    // Member (회원 관련)
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "해당 회원을 찾을 수 없습니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "M002", "이미 존재하는 아이디입니다."),

    // AttendanceConfig (출석 설정)
    DUPLICATE_ATTENDANCE_CONFIG(HttpStatus.CONFLICT, "AC001", "이미 존재하는 출석 설정입니다."),

    // Attendance (출석 관련 - 기존 A00x 코드 유지)
    INVALID_AUTH_NUMBER(HttpStatus.BAD_REQUEST, "A001", "인증번호가 올바르지 않습니다."),
    ATTENDANCE_TIME_EXPIRED(HttpStatus.BAD_REQUEST, "A002", "출석 가능 시간이 아닙니다."),
    DUPLICATE_ATTENDANCE(HttpStatus.CONFLICT, "A003", "이미 출석을 완료하셨습니다."),
    ATTENDANCE_CONFIG_NOT_FOUND(HttpStatus.NOT_FOUND, "A004", "출석 설정이 존재하지 않습니다."),
    INVALID_IP_ADDRESS(HttpStatus.FORBIDDEN, "A005", "허용되지 않은 IP 주소입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    // 1. 코드(code)를 직접 지정하는 생성자
    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    // 2. 코드를 지정하지 않으면 Enum 이름을 코드로 사용하는 생성자 (유연성 확보)
    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.code = this.name();
        this.message = message;
    }
}