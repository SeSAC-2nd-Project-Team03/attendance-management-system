package com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity;

import com.sesac2ndproject.attendancemanagementsystem.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "leave_files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LeaveFile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 신청서에 달린 파일인지 (FK) -> Leave 대신 LeaveRequest 사용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_request_id", nullable = false)
    private LeaveRequest leaveRequest;

    // 원본 파일명 (예: 진단서.pdf)
    @Column(nullable = false)
    private String originalFileName;

    // 저장된 파일명 (예: uuid_진단서.pdf) - S3 URL 등
    @Column(nullable = false)
    private String fileUrl;

    // 파일 크기
    @Column
    private Long fileSize;

    // 파일 타입 (image/png 등)
    @Column
    private String mimeType;
}