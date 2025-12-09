package com.sesac2ndproject.attendancemanagementsystem.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttendanceStatus {
    PRESENT("출석"),
    LATE("지각"),
    ABSENT("결석"),
    NONE("미체크"),  // 아직 시간이 안 돼서 안 찍은 상태
    LEAVE("조퇴"),
    OFFICIAL_LEAVE("공결");

    private final String description;
}
