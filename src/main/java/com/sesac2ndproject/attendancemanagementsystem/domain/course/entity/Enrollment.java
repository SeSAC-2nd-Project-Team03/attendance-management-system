package com.sesac2ndproject.attendancemanagementsystem.domain.course.entity;

import com.sesac2ndproject.attendancemanagementsystem.domain.member.entity.Member;
import com.sesac2ndproject.attendancemanagementsystem.global.entity.BaseTimeEntity;
import com.sesac2ndproject.attendancemanagementsystem.global.type.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Enrollment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member memberId;

    @Column(nullable = false)
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    // 추가 필드 : status 변경시각(수료 -> 이탈, 수료 -> 완료)
    private LocalDateTime statusChangedAt;

    // 추가 필드 : 상태 변경 사유(수료/이탈/중도포기 등)
    @Column(columnDefinition = "TEXT")
    private String description;

    // 상태 변경 메서드
    public void changeStatus(EnrollmentStatus newStatus, String description) {
        this.status = newStatus;
        this.description = description;
        this.statusChangedAt = LocalDateTime.now();
    }
}
