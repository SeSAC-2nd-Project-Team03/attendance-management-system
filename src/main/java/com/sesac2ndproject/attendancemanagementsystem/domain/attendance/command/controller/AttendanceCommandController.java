package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.command.controller;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.command.dto.AttendanceAutoCheckRequest;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.command.service.AttendanceCommandService;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.request.AttendanceCheckRequest;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.response.AttendanceCheckResponse;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.query.dto.MyAttendanceResponse;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.query.service.AttendanceQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 출석 관리 API
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/attendance/execute")
@RequiredArgsConstructor
@Tag(name = "Attendance (Command)", description = "출석 체크 및 상태 변경 API")
public class AttendanceCommandController {

    private final AttendanceCommandService attendanceCommandService;
    private final AttendanceQueryService attendanceQueryService;

    /**
     * 출석 체크 API (타입 직접 지정)
     */
    @PostMapping("/v1/attendances")
    @Operation(
            summary = "출석 체크",
            description = """
            학생이 출석 인증번호를 입력하여 출석을 체크합니다.
            
            **테스트 정보:**
            - memberId: 1 (테스트용 사용자 ID)
            - courseId: 1
            - 아침(MORNING) 인증번호: 1234
            - 점심(LUNCH) 인증번호: 5678
            - 저녁(DINNER) 인증번호: 9012
            
            **검증 항목:**
            1. 인증번호 일치 여부
            2. 출석 가능 시간 확인
            3. 중복 출석 방지
            
            **출석 상태:**
            - 기준 시간 이전: PRESENT (출석)
            - 기준 시간 이후: LATE (지각)
            
            **주의:** 실패해도 200 OK를 반환하며, success 필드로 성공/실패를 구분합니다.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "출석 체크 완료 (성공/실패 모두 포함)",
                            content = @Content(
                                    schema = @Schema(implementation = AttendanceCheckResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 - 출석",
                                                    value = """
                                {
                                  "success": true,
                                  "message": "출석이 정상적으로 처리되었습니다.",
                                  "checkTime": "2025-12-03T09:05:12"
                                }
                                """
                                            ),
                                            @ExampleObject(
                                                    name = "성공 - 지각",
                                                    value = """
                                {
                                  "success": true,
                                  "message": "지각 처리되었습니다.",
                                  "checkTime": "2025-12-03T09:15:12"
                                }
                                """
                                            ),
                                            @ExampleObject(
                                                    name = "실패 - 인증번호 오류",
                                                    value = """
                                {
                                  "success": false,
                                  "message": "인증번호가 올바르지 않습니다.",
                                  "checkTime": "2025-12-03T09:05:12"
                                }
                                """
                                            ),
                                            @ExampleObject(
                                                    name = "실패 - 중복 출석",
                                                    value = """
                                {
                                  "success": false,
                                  "message": "이미 MORNING 출석을 완료하셨습니다.",
                                  "checkTime": "2025-12-03T09:05:12"
                                }
                                """
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<AttendanceCheckResponse> checkAttendance(
            @Parameter(description = "출석 체크 요청 정보", required = true)
            @Valid @RequestBody AttendanceCheckRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("POST /api/v1/attendances - memberId: {}, courseId: {}, type: {}",
                request.getMemberId(), request.getCourseId(), request.getType());

        Long memberId = request.getMemberId();
        log.info("📌 요청 사용자: memberId = {}", memberId);

        String connectionIp = extractIpAddress(httpRequest);
        log.info("📌 접속 IP: {}", connectionIp);

        AttendanceCheckResponse response = attendanceCommandService.checkAttendance(
                memberId,
                request.getCourseId(),
                request.getType(),
                request.getInputNumber(),
                connectionIp
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 자동 출석 체크 API (시간 기반 타입 자동 판단)
     */
    @PostMapping("/v1/attendances/auto")
    @Operation(
            summary = "자동 출석 체크",
            description = """
            현재 시간을 기준으로 출석 타입(MORNING, LUNCH, DINNER)을 자동 판단하여 출석 체크합니다.
            
            **시간대 기준:**
            - MORNING (아침): 08:40 ~ 09:20
            - LUNCH (점심): 12:10 ~ 12:50
            - DINNER (저녁): 17:30 ~ 18:10
            
            **테스트 정보:**
            - memberId: 1 (테스트용 사용자 ID)
            - courseId: 1
            - 인증번호는 시간대에 맞게 입력
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "자동 출석 체크 완료",
                            content = @Content(
                                    schema = @Schema(implementation = AttendanceCheckResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공",
                                                    value = """
                                {
                                  "success": true,
                                  "message": "출석이 정상적으로 처리되었습니다.",
                                  "checkTime": "2025-12-03T09:05:12"
                                }
                                """
                                            ),
                                            @ExampleObject(
                                                    name = "실패 - 시간대 외",
                                                    value = """
                                {
                                  "success": false,
                                  "message": "현재는 출석 가능 시간이 아닙니다.",
                                  "checkTime": "2025-12-03T10:30:00"
                                }
                                """
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<AttendanceCheckResponse> checkAttendanceAuto(
            @Parameter(description = "자동 출석 체크 요청 정보", required = true)
            @Valid @RequestBody AttendanceAutoCheckRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("POST /api/v1/attendances/auto - memberId: {}, courseId: {}",
                request.getMemberId(), request.getCourseId());

        String connectionIp = extractIpAddress(httpRequest);
        log.info("📌 접속 IP: {}", connectionIp);

        AttendanceCheckResponse response = attendanceCommandService.checkAttendanceAuto(
                request.getMemberId(),
                request.getCourseId(),
                request.getInputNumber(),
                connectionIp
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 레거시 출석 체크 API
     */
    @PostMapping("/attendance/check-in")
    @Operation(summary = "출석 체크 (레거시)", description = "레거시 경로 호환용")
    public ResponseEntity<AttendanceCheckResponse> checkIn(
            @Valid @RequestBody AttendanceCheckRequest request,
            HttpServletRequest httpRequest
    ) {
        String connectionIp = extractIpAddress(httpRequest);
        AttendanceCheckResponse response = attendanceCommandService.checkAttendance(
                request.getMemberId(),
                request.getCourseId(),
                request.getType(),
                request.getInputNumber(),
                connectionIp
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 내 출석 조회 API
     */
    @GetMapping("/v1/attendances/me")
    @Operation(
            summary = "내 출석 조회",
            description = """
            특정 날짜의 출석 현황을 조회합니다.
            
            **상태 계산 규칙:**
            - O + O + O → 출석
            - △ + O + O → 지각
            - X + O + O → 지각
            - O + O + X → 조퇴
            - X + X + X → 결석
            """
    )
    public ResponseEntity<MyAttendanceResponse> getMyAttendance(
            @Parameter(description = "회원 ID") @RequestParam Long memberId,
            @Parameter(description = "강의 ID") @RequestParam Long courseId,
            @Parameter(description = "조회 날짜 (기본: 오늘)") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (date == null) {
            date = LocalDate.now();
        }
        log.info("GET /api/v1/attendances/me - memberId: {}, courseId: {}, date: {}", memberId, courseId, date);
        
        MyAttendanceResponse response = attendanceQueryService.getMyAttendance(memberId, courseId, date);
        return ResponseEntity.ok(response);
    }

    /**
     * IP 주소 추출
     */
    private String extractIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}