package com.sesac2ndproject.attendancemanagementsystem.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @Column(nullable = false)
    private String loginId; // 사용자 Login ID (Key 역할)

    @Column(nullable = false)
    private String token; // 실제 Refresh Token 값

    public void updateToken(String newToken) {
        this.token = newToken;
    }
}
