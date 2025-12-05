package com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity;

import com.sesac2ndproject.attendancemanagementsystem.global.type.LeaveStatus;
import com.sesac2ndproject.attendancemanagementsystem.global.type.LeaveType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leaves")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신청자 ID
    @Column(nullable = false)
    private Long memberId;

    // 휴가 타입: SICK_LEAVE(병가), VACATION_LEAVE(휴가), PERSONAL_LEAVE(개인사유), EARLY_LEAVE(조퇴)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leaveType;

    // 신청 날짜
    @Column(nullable = false)
    private LocalDate leaveDate;

    // 신청 사유
    @Column(columnDefinition = "TEXT")
    private String reason;

    // 신청 상태: PENDING(대기), APPROVED(승인), REJECTED(반려)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status;

    // 승인/반려 처리자 ID
    @Column
    private Long approvedBy;

    // 승인/반려 사유
    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    // 생성 일시
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 수정 일시
    @Column
    private LocalDateTime updatedAt;

    // 삭제 여부
    @Column(nullable = false)
    private Boolean isDeleted;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}