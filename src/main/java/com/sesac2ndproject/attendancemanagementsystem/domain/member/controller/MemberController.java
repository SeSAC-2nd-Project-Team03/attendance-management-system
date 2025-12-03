package com.sesac2ndproject.attendancemanagementsystem.domain.member.controller;

import com.sesac2ndproject.attendancemanagementsystem.domain.member.entity.Member;
import com.sesac2ndproject.attendancemanagementsystem.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member (User)", description = "일반 사용자용 회원 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "내 정보 조회", description = "로그인한 회원의 정보를 조회합니다")
    @GetMapping("/me")
    public ResponseEntity<String> getMyInfo(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok("안녕하세요, " + member.getName() + "님! (ID: " + member.getLoginId() + ")");
    }
}
