package com.sesac2ndproject.attendancemanagementsystem.domain.member.dto;

import com.sesac2ndproject.attendancemanagementsystem.domain.member.entity.Member;
import com.sesac2ndproject.attendancemanagementsystem.domain.member.service.MemberService;
import com.sesac2ndproject.attendancemanagementsystem.global.type.RoleType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberResponse {
    private Long id;
    private String loginId;
    private String name;
    private String phoneNumber;
    private String address;
    private RoleType role;

    public MemberResponse(Long id, String loginId, String name, String phoneNumber, String address, RoleType role) {
        this.id = id;
        this.loginId = loginId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
    }

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getLoginId(),
                member.getName(),
                member.getPhoneNumber(),
                member.getAddress(),
                member.getRole()
        );
    }
}
