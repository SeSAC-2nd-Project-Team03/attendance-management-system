package com.sesac2ndproject.attendancemanagementsystem.domain.leave.service;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.LeaveRequestCreateDto;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.LeaveRequestResponseDto;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity.LeaveRequest;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity.LeaveStatus;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.repository.LeaveRequestRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final MemberRepository memberRepository;

    @Value("${file.upload.path:uploads/leave-files}")
    private String uploadPath;

    /**
     * 휴가 신청 생성 (파일 포함)
     */
    @Override
    public LeaveRequestResponseDto createLeaveRequest(
            String studentLoginId,
            LeaveRequestCreateDto dto,
            MultipartFile file
    ) {
        try {
            // 회원 존재 여부 확인
            memberRepository.findByLoginId(studentLoginId)
                    .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

            // 파일 업로드
            String fileUrl = null;
            if (file != null && !file.isEmpty()) {
                fileUrl = saveFile(file);
            }

            // LeaveRequest 생성
            LeaveRequest leaveRequest = LeaveRequest.builder()
                    .studentLoginId(studentLoginId)
                    .leaveDate(dto.getLeaveDate())
                    .leaveType(dto.getLeaveType())
                    .reason(dto.getReason())
                    .fileUrl(fileUrl)
                    .status(LeaveStatus.PENDING)
                    .requestedAt(LocalDateTime.now())
                    .build();

            // DB에 저장
            LeaveRequest saved = leaveRequestRepository.save(leaveRequest);

            return LeaveRequestResponseDto.from(saved);

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 파일 저장 로직
     */
    private String saveFile(MultipartFile file) throws IOException {
        // 업로드 폴더 생성 (없으면)
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 파일명 생성 (원본명 + UUID로 중복 방지)
        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID() + "_" + originalFileName;
        Path filePath = uploadDir.resolve(uniqueFileName);

        // 파일 저장
        Files.write(filePath, file.getBytes());

        // 저장된 파일의 경로 반환
        return uploadPath + "/" + uniqueFileName;
    }

    /**
     * 내 신청 내역 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDto> getMyLeaveRequests(String studentLoginId) {
        return leaveRequestRepository
                .findByStudentLoginIdOrderByRequestedAtDesc(studentLoginId)
                .stream()
                .map(LeaveRequestResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 내 신청 상세 조회
     */
    @Override
    @Transactional(readOnly = true)
    public LeaveRequestResponseDto getLeaveDetail(Long leaveId, String studentLoginId) {
        LeaveRequest leaveRequest = leaveRequestRepository
                .findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("신청을 찾을 수 없습니다."));

        if (!leaveRequest.getStudentLoginId().equals(studentLoginId)) {
            throw new IllegalArgumentException("다른 사용자의 신청은 조회할 수 없습니다.");
        }

        return LeaveRequestResponseDto.from(leaveRequest);
    }

    /**
     * 신청 취소 (PENDING 상태만 가능)
     */
    @Override
    public void cancelLeaveRequest(Long leaveId, String studentLoginId) {
        LeaveRequest leaveRequest = leaveRequestRepository
                .findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("신청을 찾을 수 없습니다."));

        // 본인 신청만 취소 가능
        if (!leaveRequest.getStudentLoginId().equals(studentLoginId)) {
            throw new IllegalArgumentException("다른 사용자의 신청은 취소할 수 없습니다.");
        }

        // PENDING 상태만 취소 가능
        leaveRequest.cancel();
        leaveRequestRepository.save(leaveRequest);
    }

    /**
     * 신청 승인 (관리자용)
     */
    @Override
    public void approveLeaveRequest(Long leaveId, String adminLoginId) {
        LeaveRequest leaveRequest = leaveRequestRepository
                .findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("신청을 찾을 수 없습니다."));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태인 신청만 승인할 수 있습니다.");
        }

        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setProcessedAt(LocalDateTime.now());
        leaveRequest.setProcessedBy(adminLoginId);
        leaveRequestRepository.save(leaveRequest);
    }

    /**
     * 신청 반려 (관리자용)
     */
    @Override
    public void rejectLeaveRequest(Long leaveId, String adminLoginId, String rejectionReason) {
        LeaveRequest leaveRequest = leaveRequestRepository
                .findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("신청을 찾을 수 없습니다."));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태인 신청만 반려할 수 있습니다.");
        }

        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setReason(rejectionReason);
        leaveRequest.setProcessedAt(LocalDateTime.now());
        leaveRequest.setProcessedBy(adminLoginId);
        leaveRequestRepository.save(leaveRequest);
    }
}

