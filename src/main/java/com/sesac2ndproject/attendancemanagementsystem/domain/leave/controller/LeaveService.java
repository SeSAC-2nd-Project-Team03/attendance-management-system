package com.sesac2ndproject.attendancemanagementsystem.domain.leave.controller;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.request.LeaveCreateRequest;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.response.LeaveResponse;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.response.LeaveDetailResponse;
import org.springframework.web.multipart.MultipartFile; // MultipartFile import 추가

import java.util.List;

public interface LeaveService {

    /**
     * 조퇴/결석 신청 생성 (파일 포함)
     * Controller에서 전달받은 MultipartFile을 처리하여 파일 URL을 DB에 저장합니다.
     */
    LeaveDetailResponse createLeave(Long memberId, LeaveCreateRequest request, MultipartFile file);

    /**
     * 내 신청 내역 조회
     */
    List<LeaveResponse> getMyLeaves(Long memberId);

    /**
     * 내 신청 상세 조회
     */
    LeaveDetailResponse getLeaveDetail(Long leaveId, Long memberId);

    /**
     * 신청 취소 (PENDING 상태일 때만)
     */
    void cancelLeave(Long leaveId, Long memberId);

    /**
     * 신청 승인 (관리자용)
     */
    void approveLeave(Long leaveId, Long adminId);

    /**
     * 신청 반려 (관리자용)
     */
    void rejectLeave(Long leaveId, Long adminId, String rejectionReason);
}