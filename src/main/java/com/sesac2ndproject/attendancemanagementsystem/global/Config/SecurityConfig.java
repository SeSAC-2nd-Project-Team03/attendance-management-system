package com.sesac2ndproject.attendancemanagementsystem.global.Config;

import com.sesac2ndproject.attendancemanagementsystem.global.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 보안 필터 체인
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 해제 (REST API 개발 시 필수)
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 끄기 (JWT 사용)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // url별 권한 관리
                .authorizeHttpRequests(auth -> auth
                        // Swagger 관련 주소는 누구나 접속 가능 (permitAll)
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // H2 DB 콘솔도 누구나 접속 가능
                        .requestMatchers("/h2-console/**").permitAll()

                        // 1. 관리자 전용 경로 (/api/v1/admin/**) -> ADMIN 권한만 가능
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // 2. 일반 사용자 경로 (/api/v1/members/**) -> 인증된 누구나 가능 (USER, ADMIN 모두)
                        .requestMatchers("/api/v1/members/**").authenticated()

                        // 3. 인증 관련 (로그인 등) -> 누구나 가능
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // 4. 출석 관련 API -> 테스트용으로 누구나 가능 (추후 인증 필요 시 수정)
                        .requestMatchers("/api/v1/attendances/**").permitAll()
                        .requestMatchers("/api/attendance/**").permitAll()

                        // 나머지는 무조건 인증 필요
                        .anyRequest().authenticated()
                )

                // H2 Console 사용을 위한 설정 (화면 깨짐 방지)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
