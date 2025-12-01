package com.sesac2ndproject.attendancemanagementsystem.domain.auth.controller;


import com.sesac2ndproject.attendancemanagementsystem.domain.auth.dto.LoginRequest;
import com.sesac2ndproject.attendancemanagementsystem.domain.auth.dto.LoginResponse;
import com.sesac2ndproject.attendancemanagementsystem.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Auth", description = "로그인 및 회원가입 인증 API")
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Login", description = "user에게 id/pw 를 입력받아 JWT 토큰을 발급합니다")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);

        // JSON 형태로 반환하기
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
