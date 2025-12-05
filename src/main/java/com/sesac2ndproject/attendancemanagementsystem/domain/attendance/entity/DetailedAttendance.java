package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity;

import com.sesac2ndproject.attendancemanagementsystem.global.entity.BaseTimeEntity;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
/**
 * 출석 상세 로그 엔티티
 * 학생의 모든 출석 입력 시도를 기록합니다 (성공/실패 모두)
 *
 * ✅ Person 1 수정 사항:
 * - courseId 필드 추가 (필수)
 * - failReason 필드 추가 (권장)
 * - dailyAttendanceId nullable 처리
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "detailed_attendance")
public class DetailedAttendance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // DailyAttendance와 연결 (어느 날의 출석에 포함되는지)
    // ⚠️ Person 1은 이 값을 null로 저장 (Person 2가 나중에 업데이트)
    private Long dailyAttendanceId;  // ✅ nullable 처리 (NOT NULL 제약 제거)

    @Column(nullable = false)
    private Long memberId; // 누가 찍었는지

    // ✅ 추가: 어느 강의의 출석인지
//    @Column(nullable = false)
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceType type; // 아침/점심/저녁 중 무엇인지

    private String inputNumber; // 학생이 입력한 번호 ("1234")

    private LocalDateTime checkTime; // 실제 찍은 시간 (2025-11-28 09:05:12)

    private String connectionIp; // 접속 IP (검증용)

    @Column(nullable = false)
    private boolean isVerified; // 정답 여부 (true/false)

    // ✅ 추가: 실패 사유 저장 (디버깅 및 사용자 피드백용)
    @Column(length = 500)
    private String failReason;
}