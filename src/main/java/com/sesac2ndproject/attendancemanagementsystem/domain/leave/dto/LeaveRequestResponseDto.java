package com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity.LeaveRequest;
import com.sesac2ndproject.attendancemanagementsystem.global.type.LeaveStatus;
import com.sesac2ndproject.attendancemanagementsystem.global.type.LeaveType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class LeaveRequestResponseDto {

    private Long id;

    // 신청자 정보 (Member 엔티티에서 추출)
    private String studentLoginId;
    private String studentName; // 이름도 같이 보여주면 좋음

    // 신청 내용
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String fileUrl;     // 증빙서류 URL

    // 상태 및 처리 정보
    private LeaveStatus status;
    private LocalDateTime requestedAt; // 신청일시 (BaseTimeEntity)

    // 관리자 처리 결과 (null 일 수 있음)
    private String processedBy;
    private LocalDateTime processedAt;
    private String adminComment;

    /**
     * Entity -> DTO 변환 메서드 (Factory Method Pattern)
     */
    public static LeaveRequestResponseDto from(LeaveRequest entity) {
        return LeaveRequestResponseDto.builder()
                .id(entity.getId())
                .studentLoginId(entity.getMember().getLoginId()) // Member 접근
                .studentName(entity.getMember().getName())       // Member 접근 (getName이 있다고 가정)
                .leaveType(entity.getType())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .reason(entity.getReason())
                .fileUrl(entity.getFileUrl())
                .status(entity.getStatus())
                .requestedAt(entity.getCreatedAt())
                .processedBy(entity.getProcessedBy())
                .processedAt(entity.getProcessedAt())
                .adminComment(entity.getAdminComment())
                .build();
    }
}