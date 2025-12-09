package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.query.controller;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.query.dto.MyAttendanceResponse;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.query.service.AttendanceQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;


@Slf4j
@RestController
@RequestMapping("/api/v1/attendances") // CommandController와 동일한 리소스 URL
@RequiredArgsConstructor
@Tag(name = "Attendance (Query)", description = "출석 현황 조회 API")
public class AttendanceQueryController {

    private final AttendanceQueryService attendanceQueryService;

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

}
