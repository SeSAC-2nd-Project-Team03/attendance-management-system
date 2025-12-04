package com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity;

import com.sesac2ndproject.attendancemanagementsystem.domain.member.entity.Member;
import com.sesac2ndproject.attendancemanagementsystem.global.type.LeaveStatus;
import com.sesac2ndproject.attendancemanagementsystem.global.type.LeaveType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
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
    private LocalDate target_date;  // 신청 날짜
    private LeaveType type;         // 이유 타입 : 조퇴/결석
    private String reason;          // 사유
    private String fileUrl;         // 증빙서류 경로
    private LeaveStatus status;     // 대기/승인/반려
    private String adminComment;    // 관리자 코멘트
    private LocalDateTime processedAt;  // 처리일시
}
