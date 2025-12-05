package com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity.LeaveRequest;
import com.sesac2ndproject.attendancemanagementsystem.global.type.LeaveStatus;
import com.sesac2ndproject.attendancemanagementsystem.global.type.LeaveType;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequestResponseDto {
    private Long id;
    private LeaveType leaveType;
    private String reason;
    private String fileUrl;
    private LeaveStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private String processedBy;

    // Entity를 DTO로 변환
    public static LeaveRequestResponseDto from(LeaveRequest entity) {
        return LeaveRequestResponseDto.builder()
                .id(entity.getId())
                .leaveType(entity.getLeaveType())
                .reason(entity.getReason())
                .fileUrl(entity.getFileUrl())
                .status(entity.getStatus())
                .requestedAt(entity.getRequestedAt())
                .processedAt(entity.getProcessedAt())
                .processedBy(entity.getProcessedBy())
                .build();
    }
}