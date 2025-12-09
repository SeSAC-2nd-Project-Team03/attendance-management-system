package com.sesac2ndproject.attendancemanagementsystem.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    USER("일반 사용자"),
    ADMIN("관리자");

    private final String description;
}
