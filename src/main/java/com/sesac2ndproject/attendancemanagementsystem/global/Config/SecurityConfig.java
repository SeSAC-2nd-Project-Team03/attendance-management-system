package com.sesac2ndproject.attendancemanagementsystem.global.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 해제 (REST API 개발 시 필수)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. 요청 주소별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 🟢 Swagger 관련 주소는 누구나 접속 가능 (permitAll)
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // 🟢 H2 DB 콘솔도 누구나 접속 가능
                        .requestMatchers("/h2-console/**").permitAll()

                        // 🟢 회원가입/로그인 등 Auth 관련도 누구나 접속 가능
                        .requestMatchers("/api/v1/auth/**", "/api/v1/admin/**").permitAll()

                        // 🔴 나머지는 무조건 인증 필요
                        .anyRequest().authenticated()
                )

                // 3. H2 Console 사용을 위한 설정 (화면 깨짐 방지)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }
}
