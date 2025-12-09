package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.common.dto.event;

import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * 출석 기록 생성 이벤트
 *
 * Person 1이 DetailedAttendance를 저장한 후 발행하는 이벤트
 * Person 2가 이 이벤트를 받아서 DailyAttendance 상태를 계산합니다.
 *
 * ✨ 이것이 Person 1과 Person 2 사이의 유일한 계약(Contract)입니다.
 *
 * ⚠️ 주의사항:
 * - 이 클래스는 Person 1, Person 2 모두 참조 가능
 * - 필드 추가/수정 시 양쪽에 영향을 주므로 신중하게 결정
 * - 불변 객체로 설계 (Getter만, Setter 없음)
 */
@Getter
@AllArgsConstructor
@ToString
public class AttendanceLogCreatedEvent {

    /**
     * 출석을 기록한 회원 ID
     */
    private final Long memberId;

    /**
     * 출석 기록이 속한 과정(강의) ID
     */
    private final Long courseId;

    /**
     * 출석 날짜 (년-월-일)
     */
    private final LocalDate targetDate;

    /**
     * 출석 타입 (MORNING, LUNCH, DINNER)
     */
    private final AttendanceType type;

    /**
     * 검증 성공 여부
     * - true: 인증번호 정답, 시간 내 체크
     * - false: 인증번호 오답, 시간 초과 등
     */
    private final boolean isVerified;

    /**
     * 생성된 DetailedAttendance의 ID
     * Person 2가 필요한 경우 이 ID로 상세 로그를 조회 가능
     */
    private final Long detailedAttendanceId;

    /**
     * 편의 메서드: 이벤트가 성공한 출석 기록인지 확인
     */
    public boolean isSuccessfulAttendance() {
        return isVerified;
    }

    /**
     * 편의 메서드: 이벤트가 실패한 출석 시도인지 확인
     */
    public boolean isFailedAttempt() {
        return !isVerified;
    }
}