package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity;

import com.sesac2ndproject.attendancemanagementsystem.global.entity.BaseTimeEntity;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "detailed_attendance")
public class DetailedAttendance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // DailyAttendance와 연결 (어느 날의 출석에 포함되는지)
    // 객체 참조 대신 ID를 쓰기로 했으므로 Long으로 선언
    @Column(nullable = false)
    private Long dailyAttendanceId;

    @Column(nullable = false)
    private Long memberId; // 누가 찍었는지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceType type; // 아침/점심/저녁 중 무엇인지

    private String inputNumber; // 학생이 입력한 번호 ("1234")

    private LocalDateTime checkTime; // 실제 찍은 시간 (2025-11-28 09:05:12)

    private String connectionIp; // 접속 IP (검증용)

    @Column(nullable = false)
    private boolean isVerified; // 정답 여부 (true/false)

    // 생성자 (출석 체크 시 생성)
    public DetailedAttendance(Long dailyAttendanceId, Long memberId, AttendanceType type, String inputNumber, LocalDateTime checkTime, String connectionIp, boolean isVerified) {
        this.dailyAttendanceId = dailyAttendanceId;
        this.memberId = memberId;
        this.type = type;
        this.inputNumber = inputNumber;
        this.checkTime = checkTime;
        this.connectionIp = connectionIp;
        this.isVerified = isVerified;
    }
}
