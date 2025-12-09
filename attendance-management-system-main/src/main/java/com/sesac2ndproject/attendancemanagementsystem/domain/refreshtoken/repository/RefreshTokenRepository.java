package com.sesac2ndproject.attendancemanagementsystem.domain.refreshtoken.repository;

import com.sesac2ndproject.attendancemanagementsystem.domain.refreshtoken.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
