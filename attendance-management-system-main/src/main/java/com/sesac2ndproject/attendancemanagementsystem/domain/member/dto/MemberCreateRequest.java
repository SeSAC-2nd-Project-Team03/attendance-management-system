package com.sesac2ndproject.attendancemanagementsystem.domain.member.dto;

import com.sesac2ndproject.attendancemanagementsystem.global.type.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberCreateRequest {
    @NotBlank(message = "아이디는 필수 입니다")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 입니다")
    private String password;

    @NotBlank(message = "이름은 필수 입니다")
    private String name;

    private String phoneNumber;

    @NotNull(message = "권한 입력(ROLE_ADMIN/ROLE_USER)은 필수 입니다")
    private RoleType role;

}
