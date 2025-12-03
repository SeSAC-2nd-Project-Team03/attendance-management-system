package com.sesac2ndproject.attendancemanagementsystem.domain.member.service;

import com.sesac2ndproject.attendancemanagementsystem.domain.member.dto.MemberCreateRequest;
import com.sesac2ndproject.attendancemanagementsystem.domain.member.dto.MemberResponse;
import com.sesac2ndproject.attendancemanagementsystem.domain.member.entity.Member;
import com.sesac2ndproject.attendancemanagementsystem.domain.member.repository.MemberRepository;
import com.sesac2ndproject.attendancemanagementsystem.global.error.CustomException;
import com.sesac2ndproject.attendancemanagementsystem.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long createMember(MemberCreateRequest request) {
        // 중복 검사
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new CustomException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 엔티티 생성
        Member member = Member.builder()
                .loginId(request.getLoginId())
                .password(request.getPassword())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .build();

        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(MemberResponse::from)
                .toList();
    }

}
