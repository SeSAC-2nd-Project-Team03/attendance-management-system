package com.sesac2ndproject.attendancemanagementsystem.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 1. 비즈니스 로직 에러 (CustomException) 처리
     * 예: 중복 아이디, 회원 없음, 비밀번호 불일치 등
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("handleCustomException: {}", e.getErrorCode());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    /**
     * 2. @Valid 유효성 검사 실패 처리
     * AdminMemberController 등에서 @RequestBody @Valid 실패 시 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValid: {}", e.getMessage());

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "VALIDATION_FAILED",
                "message", "입력값이 유효하지 않습니다.",
                "details", errors
        ));
    }

    /**
     * 3. 권한 없음 (AccessDeniedException)
     * SecurityConfig에서 권한이 없는 요청을 했을 때 발생
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("handleAccessDeniedException: {}", e.getMessage());
        return ErrorResponse.toResponseEntity(ErrorCode.FORBIDDEN);
    }

    /**
     * 4. 인증 실패 (AuthenticationException)
     * 로그인 실패 등
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        log.error("handleAuthenticationException: {}", e.getMessage());
        return ErrorResponse.toResponseEntity(ErrorCode.UNAUTHORIZED);
    }

    /**
     * 5. 그 외 모든 알 수 없는 예외 (Exception)
     * 500 Internal Server Error 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("handleException: ", e);
        return ErrorResponse.toResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
