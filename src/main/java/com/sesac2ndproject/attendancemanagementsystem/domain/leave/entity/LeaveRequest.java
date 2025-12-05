package com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity;

import com.sesac2ndproject.attendancemanagementsystem.domain.member.entity.Member;
import com.sesac2ndproject.attendancemanagementsystem.global.type.LeaveStatus;
import com.sesac2ndproject.attendancemanagementsystem.global.type.LeaveType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member memberId;        // 신청한 멤버의 ID(FK)

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

    private LocalDate target_date;  // 신청 날짜
    private LeaveType type;         // 이유 타입 : 조퇴/결석
    private String adminComment;    // 관리자 코멘트


    public void cancel() {
        if (this.status != LeaveStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태인 신청만 취소할 수 있습니다.");
        }
        this.status = LeaveStatus.CANCELLED;
    }

    // Team D : 승인 처리 메서드
    public void approve() {
        this.status = LeaveStatus.APPROVED; // 상태를 승인으로 변경
        this.processedAt = LocalDateTime.now(); // 처리 시간 기록
        this.adminComment = "관리자에 의해 승인되었습니다."; // 관리자 코멘트
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