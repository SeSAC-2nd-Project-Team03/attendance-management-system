package com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.response;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity.Leave;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveResponse {

    private Long id;
    private String leaveType;
    private LocalDate leaveDate;
    private String reason;
    private String status;  // PENDING, APPROVED, REJECTED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LeaveResponse from(Leave leave) {
        return LeaveResponse.builder()
                .id(leave.getId())
                .leaveType(leave.getLeaveType().name())
                .leaveDate(leave.getLeaveDate())
                .reason(leave.getReason())
                .status(leave.getStatus().name())
                .createdAt(leave.getCreatedAt())
                .updatedAt(leave.getUpdatedAt())
                .build();
    }
}