package com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String studentLoginId;  // PK ID 대신 LOGIN ID 사용

    @Column(nullable = false)
    private LocalDate leaveDate;  // 휴가 날짜 추가

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(length = 500)
    private String fileUrl;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LeaveStatus status = LeaveStatus.PENDING;

    @Column
    private LocalDateTime processedAt;

    @Column
    private String processedBy;

    public void cancel() {
        if (this.status != LeaveStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태인 신청만 취소할 수 있습니다.");
        }
        this.status = LeaveStatus.CANCELLED;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
    }
}