package com.sesac2ndproject.attendancemanagementsystem.domain.leave.controller;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.service.LeaveService;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.request.LeaveCreateRequest;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.response.LeaveResponse;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.response.LeaveDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/leaves")
@RequiredArgsConstructor
@Slf4j
public class LeaveController {

    private final LeaveService leaveService;

    /**
     * 조퇴/결석 신청 생성
     * POST /api/v1/leaves
     */
    @PostMapping
    public ResponseEntity<?> createLeave(
            @Valid @ModelAttribute LeaveCreateRequest request,
            Authentication authentication) {

        try {
            Long memberId = Long.parseLong(authentication.getName());
            log.info("조퇴/결석 신청 API 호출 - memberId: {}", memberId);

            LeaveDetailResponse response = leaveService.createLeave(memberId, request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "신청이 정상적으로 등록되었습니다");
            result.put("data", response);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(result);

        } catch (Exception e) {
            log.error("조퇴/결석 신청 API 오류", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 내 신청 내역 조회
     * GET /api/v1/leaves/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyLeaves(Authentication authentication) {
        try {
            Long memberId = Long.parseLong(authentication.getName());
            log.info("내 신청 내역 조회 API 호출 - memberId: {}", memberId);

            List<LeaveResponse> leaves = leaveService.getMyLeaves(memberId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "신청 내역 조회 완료");
            result.put("data", leaves);
            result.put("count", leaves.size());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("내 신청 내역 조회 API 오류", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 신청 상세 조회
     * GET /api/v1/leaves/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getLeaveDetail(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            Long memberId = Long.parseLong(authentication.getName());
            log.info("신청 상세 조회 API 호출 - leaveId: {}, memberId: {}", id, memberId);

            LeaveDetailResponse leave = leaveService.getLeaveDetail(id, memberId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "신청 상세 조회 완료");
            result.put("data", leave);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("신청 상세 조회 API 오류", e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 신청 취소
     * DELETE /api/v1/leaves/{id}
     * PENDING 상태일 때만 취소 가능
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelLeave(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            Long memberId = Long.parseLong(authentication.getName());
            log.info("신청 취소 API 호출 - leaveId: {}, memberId: {}", id, memberId);

            leaveService.cancelLeave(id, memberId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "신청이 취소되었습니다");

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("신청 취소 API 오류", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 에러 응답 생성 헬퍼 메서드
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        return error;
    }
}