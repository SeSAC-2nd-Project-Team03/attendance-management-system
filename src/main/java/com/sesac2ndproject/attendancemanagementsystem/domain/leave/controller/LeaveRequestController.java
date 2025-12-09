package com.sesac2ndproject.attendancemanagementsystem.domain.leave.controller;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.LeaveRequestCreateDto;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.LeaveRequestResponseDto;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.service.LeaveRequestService;
import com.sesac2ndproject.attendancemanagementsystem.global.type.LeaveType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    /**
     * [사용자] 휴가 신청
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LeaveRequestResponseDto> createLeaveRequest(
            @RequestParam("leaveDate") String leaveDateStr,
            @RequestParam("reason") String reason,
            @RequestParam("leaveType") LeaveType leaveType,
            @RequestPart(value = "evidenceFile", required = false) MultipartFile evidenceFile,
            @RequestHeader("Student-Login-Id") String studentLoginId
    ) {
        // 1. 파일 검증 (파일이 있을 경우에만)
        if (evidenceFile != null && !evidenceFile.isEmpty()) {
            if (!isValidFileType(evidenceFile.getContentType())) {
                throw new IllegalArgumentException("이미지(jpg, png) 또는 PDF 파일만 첨부 가능합니다.");
            }
        }

        // 2. 날짜 파싱
        LocalDate leaveDate;
        if (leaveDateStr.contains("-")) {
            leaveDate = LocalDate.parse(leaveDateStr);
        } else {
            leaveDate = LocalDate.parse(leaveDateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        }

        // 3. DTO 생성
        LeaveRequestCreateDto dto = LeaveRequestCreateDto.builder()
                .startDate(leaveDate) // 시작일 설정
                .endDate(leaveDate)   // 종료일도 같은 날짜로 설정 (1일 휴가인 경우)
                .leaveType(leaveType)
                .reason(reason)
                .build();

        LeaveRequestResponseDto response =
                leaveRequestService.createLeaveRequest(studentLoginId, dto, evidenceFile);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * [사용자] 내 신청 내역 조회
     */
    @GetMapping("/me")
    public ResponseEntity<List<LeaveRequestResponseDto>> getMyLeaveRequests(
            @RequestHeader("Student-Login-Id") String studentLoginId
    ) {
        List<LeaveRequestResponseDto> requests =
                leaveRequestService.getMyLeaveRequests(studentLoginId);
        return ResponseEntity.ok(requests);
    }

    /**
     * [사용자] 신청 취소 (파일 삭제 포함)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelLeaveRequest(
            @PathVariable Long id,
            @RequestHeader("Student-Login-Id") String studentLoginId
    ) {
        leaveRequestService.cancelLeaveRequest(id, studentLoginId);
        return ResponseEntity.noContent().build();
    }

    // ================= [관리자 기능] ================= //

    /**
     * [관리자] 신청 승인
     * PATCH /api/v1/leave-requests/{id}/approve
     */
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approveLeaveRequest(
            @PathVariable Long id,
            @RequestHeader("Admin-Login-Id") String adminLoginId // 관리자 ID 헤더로 가정
    ) {
        leaveRequestService.approveLeaveRequest(id, adminLoginId);
        return ResponseEntity.ok().build();
    }

    /**
     * [관리자] 신청 반려
     * PATCH /api/v1/leave-requests/{id}/reject
     * Body: 반려 사유 (String or JSON) - 여기선 간단히 RequestParam으로 처리
     */
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Void> rejectLeaveRequest(
            @PathVariable Long id,
            @RequestParam("reason") String rejectionReason,
            @RequestHeader("Admin-Login-Id") String adminLoginId
    ) {
        leaveRequestService.rejectLeaveRequest(id, adminLoginId, rejectionReason);
        return ResponseEntity.ok().build();
    }

    // ================= [내부 유틸 메서드] ================= //

    /**
     * 파일 타입 검증 로직 (여기 있네!)
     */
    private boolean isValidFileType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.startsWith("image/") ||
                contentType.equals("application/pdf");
    }
}