package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.controller;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.AttendanceRequest;
// ⚠️ 중요: AttendanceCheckResponse를 import 해야 합니다.
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.response.AttendanceCheckResponse;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.service.AttendanceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    // 1️⃣ 반환 타입 변경: ResponseEntity<AttendanceResponse> -> ResponseEntity<AttendanceCheckResponse>
    public ResponseEntity<AttendanceCheckResponse> checkIn(
            @RequestBody AttendanceRequest request,
            HttpServletRequest servletRequest
    ) {
        String connectionIp = servletRequest.getRemoteAddr();

        // 2️⃣ 변수 타입 변경: AttendanceResponse -> AttendanceCheckResponse
        AttendanceCheckResponse response = attendanceService.checkAttendance(
                request.getMemberId(),
                request.getCourseId(),
                request.getType(),
                request.getInputNumber(),
                connectionIp
        );

        return ResponseEntity.ok(response);
    }
}