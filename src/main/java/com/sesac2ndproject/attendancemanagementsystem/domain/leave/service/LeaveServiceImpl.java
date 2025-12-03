package com.sesac2ndproject.attendancemanagementsystem.domain.leave.service;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.request.LeaveCreateRequest;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.response.LeaveDetailResponse;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.response.LeaveResponse;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity.Leave;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity.LeaveFile;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity.LeaveStatus;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity.LeaveType;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.exception.LeaveException;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.repository.LeaveFileRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.repository.LeaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final LeaveFileRepository leaveFileRepository;
    private final FileService fileService;

    @Override
    public LeaveDetailResponse createLeave(Long memberId, LeaveCreateRequest request) {
        log.info("조퇴/결석 신청 생성 시작 - memberId: {}, leaveType: {}", memberId, request.getLeaveType());

        try {
            // Leave 엔티티 생성
            Leave leave = Leave.builder()
                    .memberId(memberId)
                    .leaveType(LeaveType.valueOf(request.getLeaveType()))
                    .leaveDate(request.getLeaveDate())
                    .reason(request.getReason())
                    .status(LeaveStatus.PENDING)
                    .isDeleted(false)
                    .build();

            leave = leaveRepository.save(leave);
            log.info("Leave 엔티티 저장 완료 - leaveId: {}", leave.getId());

            // 파일 처리
            List<LeaveFile> files = new ArrayList<>();
            if (request.getFiles() != null && !request.getFiles().isEmpty()) {
                for (MultipartFile multipartFile : request.getFiles()) {
                    Map<String, Object> fileInfo = fileService.saveFile(multipartFile);

                    LeaveFile leaveFile = new LeaveFile();
                    leaveFile.setLeave(leave);
                    leaveFile.setOriginalFileName(multipartFile.getOriginalFilename());
                    leaveFile.setStoredFileName((String) fileInfo.get("storedFileName"));
                    leaveFile.setFilePath((String) fileInfo.get("filePath"));
                    leaveFile.setFileSize((Long) fileInfo.get("fileSize"));
                    leaveFile.setMimeType((String) fileInfo.get("mimeType"));

                    files.add(leaveFileRepository.save(leaveFile));
                }
                log.info("파일 저장 완료 - 개수: {}", files.size());
            }

            return LeaveDetailResponse.from(leave, files);

        } catch (Exception e) {
            log.error("조퇴/결석 신청 생성 중 오류 발생", e);
            throw new LeaveException("신청을 생성할 수 없습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveResponse> getMyLeaves(Long memberId) {
        log.info("내 신청 내역 조회 - memberId: {}", memberId);

        List<Leave> leaves = leaveRepository.findByMemberIdAndIsDeletedFalse(memberId);

        return leaves.stream()
                .map(LeaveResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveDetailResponse getLeaveDetail(Long leaveId, Long memberId) {
        log.info("신청 상세 조회 - leaveId: {}, memberId: {}", leaveId, memberId);

        Leave leave = leaveRepository.findByIdAndMemberIdAndIsDeletedFalse(leaveId, memberId)
                .orElseThrow(() -> new LeaveException("신청을 찾을 수 없습니다"));

        List<LeaveFile> files = leaveFileRepository.findByLeaveId(leaveId);

        return LeaveDetailResponse.from(leave, files);
    }

    @Override
    public void cancelLeave(Long leaveId, Long memberId) {
        log.info("신청 취소 - leaveId: {}, memberId: {}", leaveId, memberId);

        Leave leave = leaveRepository.findByIdAndMemberIdAndIsDeletedFalse(leaveId, memberId)
                .orElseThrow(() -> new LeaveException("신청을 찾을 수 없습니다"));

        // PENDING 상태일 때만 취소 가능
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new LeaveException("PENDING 상태일 때만 취소할 수 있습니다. 현재 상태: " + leave.getStatus());
        }

        // 소프트 삭제
        leave.setIsDeleted(true);
        leaveRepository.save(leave);

        // 첨부 파일 삭제
        List<LeaveFile> files = leaveFileRepository.findByLeaveId(leaveId);
        for (LeaveFile file : files) {
            fileService.deleteFile(file.getStoredFileName());
        }

        log.info("신청 취소 완료 - leaveId: {}", leaveId);
    }

    @Override
    public void approveLeave(Long leaveId, Long adminId) {
        log.info("신청 승인 - leaveId: {}, adminId: {}", leaveId, adminId);

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveException("신청을 찾을 수 없습니다"));

        leave.setStatus(LeaveStatus.APPROVED);
        leave.setApprovedBy(adminId);
        leaveRepository.save(leave);

        log.info("신청 승인 완료 - leaveId: {}", leaveId);
    }

    @Override
    public void rejectLeave(Long leaveId, Long adminId, String rejectionReason) {
        log.info("신청 반려 - leaveId: {}, adminId: {}", leaveId, adminId);

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveException("신청을 찾을 수 없습니다"));

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setApprovedBy(adminId);
        leave.setRejectionReason(rejectionReason);
        leaveRepository.save(leave);

        log.info("신청 반려 완료 - leaveId: {}", leaveId);
    }
}