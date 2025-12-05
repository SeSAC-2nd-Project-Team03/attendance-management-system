package com.sesac2ndproject.attendancemanagementsystem.domain.leave.service;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.LeaveRequestCreateDto;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.LeaveRequestResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LeaveRequestService {

    /**
     * 휴가 신청 생성 (파일 포함)
     */
    LeaveRequestResponseDto createLeaveRequest(String studentLoginId, LeaveRequestCreateDto request, MultipartFile file);

    /**
     * 내 신청 내역 조회
     */
    List<LeaveRequestResponseDto> getMyLeaveRequests(String studentLoginId);

    /**
     * 내 신청 상세 조회
     */
    LeaveRequestResponseDto getLeaveDetail(Long leaveId, String studentLoginId);

    /**
     * 신청 취소 (PENDING 상태일 때만)
     */
    void cancelLeaveRequest(Long leaveId, String studentLoginId);

    /**
     * 신청 승인 (관리자용)
     */
    void approveLeaveRequest(Long leaveId, String adminLoginId);

    /**
     * 신청 반려 (관리자용)
     */
    void rejectLeaveRequest(Long leaveId, String adminLoginId, String rejectionReason);
}