package com.sesac2ndproject.attendancemanagementsystem.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberUpdateRequest {
    private String password;
    private String phonenumber;
    private String address;
}
