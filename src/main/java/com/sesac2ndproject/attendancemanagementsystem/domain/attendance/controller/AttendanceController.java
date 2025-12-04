package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.controller;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.AttendanceConfigCreateRequest;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.service.AttendanceService;
import com.sesac2ndproject.attendancemanagementsystem.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "Attendance (Admin)", description = "출석 설정 관리 API")
@RestController
@RequestMapping("/api/v1/admin/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Operation(summary = "출석 설정 생성", description = "관리자가 반, 날짜, 시간별 출석 인증번호를 설정합니다.")
    @PostMapping("/config")
    public ResponseEntity<Void> createAttendanceConfig(
            @Valid @RequestBody AttendanceConfigCreateRequest request,
            @AuthenticationPrincipal Member admin
            ) {

        Long configId = attendanceService.createAttendanceConfig(request, admin.getId());

        return ResponseEntity.created(URI.create("/api/v1/admin/attendance/config/" + configId)).build();

    }

}
