package com.sesac2ndproject.attendancemanagementsystem.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttendanceType {
    MORNING("아침"),
    LUNCH("점심"),
    DINNER("저녁");

    private final String description;
}
