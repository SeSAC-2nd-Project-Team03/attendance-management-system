package com.sesac2ndproject.attendancemanagementsystem.domain.auth.service;

import com.sesac2ndproject.attendancemanagementsystem.domain.auth.dto.LoginRequest;
import com.sesac2ndproject.attendancemanagementsystem.domain.member.entity.Member;
import com.sesac2ndproject.attendancemanagementsystem.domain.member.repository.MemberRepository;
import com.sesac2ndproject.attendancemanagementsystem.global.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    @Transactional(readOnly = true)
    public String login(LoginRequest request) {
        // 아이디로 회원 찾기
        Member member = memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다"));

        // 비밀번호 확인
        if(!(passwordEncoder.matches(request.getPassword(), member.getPassword()))) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }

        // 인증 성공 시 토큰 생성
        return jwtTokenProvider.createToken(request.getLoginId(), member.getRole().name());
    }

}
