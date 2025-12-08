package com.sesac2ndproject.attendancemanagementsystem.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthRequest {
    @NotBlank(message = "아이디를 입력하세요")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력하세요")
    private String password;
}
