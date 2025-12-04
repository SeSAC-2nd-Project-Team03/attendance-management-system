package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.service;

import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceStatus.*;

/**
 * 출석 상태 계산 규칙
 * 
 * 사용법: AttendanceRule.calculate(아침상태, 점심상태, 저녁상태)
 * 
 * ⚠️ 저녁은 지각이 없음 (지각 → 출석 처리)
 */
@Getter
@RequiredArgsConstructor
public enum AttendanceRule {

    // 출석
    ALL_PRESENT(PRESENT, PRESENT, PRESENT, PRESENT),

    // 지각
    MORNING_LATE(LATE, PRESENT, PRESENT, LATE),
    LUNCH_LATE(PRESENT, LATE, PRESENT, LATE),
    BOTH_LATE(LATE, LATE, PRESENT, LATE),
    MORNING_ABSENT(ABSENT, PRESENT, PRESENT, LATE),
    MORNING_ABSENT_LUNCH_LATE(ABSENT, LATE, PRESENT, LATE),

    // 조퇴
    LEAVE_1(PRESENT, PRESENT, ABSENT, LEAVE),
    LEAVE_2(LATE, PRESENT, ABSENT, LEAVE),
    LEAVE_3(PRESENT, LATE, ABSENT, LEAVE),
    LEAVE_4(LATE, LATE, ABSENT, LEAVE),
    LEAVE_5(PRESENT, ABSENT, ABSENT, LEAVE),
    LEAVE_6(LATE, ABSENT, ABSENT, LEAVE),

    // 결석
    ALL_ABSENT(ABSENT, ABSENT, ABSENT, ABSENT),
    ONLY_DINNER(ABSENT, ABSENT, PRESENT, ABSENT),
    MIDDLE_SKIP_1(PRESENT, ABSENT, PRESENT, ABSENT),
    MIDDLE_SKIP_2(LATE, ABSENT, PRESENT, ABSENT);

    private final AttendanceStatus morning;
    private final AttendanceStatus lunch;
    private final AttendanceStatus dinner;
    private final AttendanceStatus result;

    /**
     * 출석 상태 계산
     */
    public static AttendanceStatus calculate(AttendanceStatus morning, AttendanceStatus lunch, AttendanceStatus dinner) {
        // 미체크가 있으면 아직 진행 중
        if (morning == null || lunch == null || dinner == null ||
            morning == NONE || lunch == NONE || dinner == NONE) {
            return NONE;
        }

        // 저녁 지각은 출석으로 처리
        AttendanceStatus dinnerFixed = (dinner == LATE) ? PRESENT : dinner;

        // 규칙 찾기
        for (AttendanceRule rule : values()) {
            if (rule.morning == morning && rule.lunch == lunch && rule.dinner == dinnerFixed) {
                return rule.result;
            }
        }

        return ABSENT;
    }
}

