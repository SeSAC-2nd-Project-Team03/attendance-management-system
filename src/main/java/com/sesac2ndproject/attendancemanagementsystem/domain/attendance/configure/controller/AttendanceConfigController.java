package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.configure.controller;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.configure.dto.AttendanceConfigCreateRequest;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.configure.entity.AttendanceConfig;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.configure.service.AttendanceConfigService;
import com.sesac2ndproject.attendancemanagementsystem.domain.member.entity.Member;
import com.sesac2ndproject.attendancemanagementsystem.global.response.ApiResponse;
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
@RequestMapping("/api/v1/admin/attendance-configs")
@RequiredArgsConstructor
public class AttendanceConfigController {

    private final AttendanceConfigService attendanceConfigService;

    @Operation(summary = "출석 설정 생성", description = "관리자가 반, 날짜, 시간별 출석 인증번호를 설정합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createAttendanceConfig(
            @Valid @RequestBody AttendanceConfigCreateRequest request
    ) {

        Long configId = attendanceConfigService.createAttendanceConfig(request);

        return ResponseEntity.created(URI.create("/api/v1/admin/attendance/config/" + configId)).build();
    }
}
