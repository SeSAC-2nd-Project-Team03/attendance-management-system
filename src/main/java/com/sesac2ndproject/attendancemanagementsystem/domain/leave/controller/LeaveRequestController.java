package com.sesac2ndproject.attendancemanagementsystem.domain.leave.controller;

import com.sesac2ndproject.attendancemanagementsystem.global.type.LeaveType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.LeaveRequestCreateDto;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.LeaveRequestResponseDto;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
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
     * 휴가 신청
     * POST /api/v1/leave-requests
     *
     * Parameters:
     * - leaveDate: 휴가 날짜 (yyyy-MM-dd 또는 yyyyMMdd)
     * - reason: 신청 사유
     * - leaveType: 휴가 타입 (EARLY_LEAVE 또는 ABSENCE)
     * - evidenceFile: 증빙 서류 (이미지/PDF) - 필수
     * - Student-Login-Id: 회원 LOGIN ID (헤더)
     */
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<LeaveRequestResponseDto> createLeaveRequest(
            @RequestParam("leaveDate") String leaveDateStr,
            @RequestParam("reason") String reason,
            @RequestParam("leaveType") LeaveType leaveType,
            @RequestParam("evidenceFile") MultipartFile evidenceFile,
            @RequestHeader("Student-Login-Id") String studentLoginId
    ) {
        try {
            // 증빙 서류 필수 확인
            if (evidenceFile == null || evidenceFile.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // 파일 타입 검증 (이미지/PDF만 허용)
            String contentType = evidenceFile.getContentType();
            if (!isValidFileType(contentType)) {
                throw new IllegalArgumentException("이미지 또는 PDF 파일만 첨부할 수 있습니다.");
            }

            // String을 LocalDate로 변환 (yyyyMMdd 또는 yyyy-MM-dd 형식 모두 지원)
            LocalDate leaveDate;
            if (leaveDateStr.contains("-")) {
                leaveDate = LocalDate.parse(leaveDateStr); // yyyy-MM-dd
            } else {
                leaveDate = LocalDate.parse(leaveDateStr,
                        DateTimeFormatter.ofPattern("yyyyMMdd")); // yyyyMMdd
            }

            LeaveRequestCreateDto dto = LeaveRequestCreateDto.builder()
                    .leaveDate(leaveDate)
                    .leaveType(leaveType)
                    .reason(reason)
                    .file(evidenceFile)
                    .build();

            LeaveRequestResponseDto response =
                    leaveRequestService.createLeaveRequest(studentLoginId, dto, evidenceFile);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 내 신청 내역 조회
     * GET /api/v1/leave-requests/me
     *
     * Headers:
     * - Student-Login-Id: 회원 LOGIN ID
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
     * 신청 취소 (PENDING 상태만 가능)
     * DELETE /api/v1/leave-requests/{id}
     *
     * Path Variables:
     * - id: 신청 ID
     *
     * Headers:
     * - Student-Login-Id: 회원 LOGIN ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelLeaveRequest(
            @PathVariable Long id,
            @RequestHeader("Student-Login-Id") String studentLoginId
    ) {
        leaveRequestService.cancelLeaveRequest(id, studentLoginId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 파일 타입 검증 (이미지/PDF만 허용)
     */
    private boolean isValidFileType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.startsWith("image/") ||
                contentType.equals("application/pdf");
    }
}