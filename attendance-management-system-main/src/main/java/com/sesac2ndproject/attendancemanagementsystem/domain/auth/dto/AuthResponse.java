package com.sesac2ndproject.attendancemanagementsystem.domain.auth.dto;

import com.sesac2ndproject.attendancemanagementsystem.global.type.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long id;
    private String loginId;
    private String name;
    private RoleType role;
}
