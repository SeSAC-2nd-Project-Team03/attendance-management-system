package com.sesac2ndproject.attendancemanagementsystem.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnrollmentStatus {
    ACTIVE("수강중"),
    COMPLETED("수료"),
    DROPPED("중도포기"),
    WAITING("승인대기");

    private final String description;
}
