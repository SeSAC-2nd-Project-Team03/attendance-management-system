package com.sesac2ndproject.attendancemanagementsystem.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LeaveType {
    EARLY_LEAVE("조퇴"),
    ABSENCE("결석/공가"),
    SICK_LEAVE("병가");

    private final String description;

}
