package com.sesac2ndproject.attendancemanagementsystem.domain.auth.repository;

import com.sesac2ndproject.attendancemanagementsystem.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
