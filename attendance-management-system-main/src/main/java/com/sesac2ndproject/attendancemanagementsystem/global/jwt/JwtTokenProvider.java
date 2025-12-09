package com.sesac2ndproject.attendancemanagementsystem.global.jwt;

import com.sesac2ndproject.attendancemanagementsystem.domain.member.entity.Member;
import com.sesac2ndproject.attendancemanagementsystem.domain.member.repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    // application.yml의 설정값을 가져옵니다. 없으면 기본값 사용
    @Value("${jwt.secret:default_secret_key_must_be_over_256_bits_length_for_security}")
    private String secretKey;

    private Key key;

    // 만료 시간 설정
    private final long ACCESS_TOKEN_VALID_TIME = 1000L * 60 * 60; // 1시간 (밀리초)
    private final long REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 7; // 7일 (밀리초)

    private final MemberRepository memberRepository;

    // 빈 생성 후 키 초기화
    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 1. Access Token 생성
    public String createAccessToken(String loginId, String role) {
        Claims claims = Jwts.claims().setSubject(loginId);
        claims.put("role", role);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Refresh Token 생성
    public String createRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 3. 토큰에서 인증 정보 조회 (DB 조회 포함)
    public Authentication getAuthentication(String token) {
        // 토큰에서 loginId 추출
        String loginId = this.getLoginId(token);

        // DB에서 회원 정보 조회
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // Authentication 객체 생성 (SecurityContext에 저장용)
        return new UsernamePasswordAuthenticationToken(
                member,
                "",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()))
        );
    }

    // 4. 토큰에서 회원 정보(LoginId) 추출
    public String getLoginId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 5. Request Header에서 토큰 추출 "Authorization: Bearer <token>"
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 6. 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}