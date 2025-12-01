package com.sesac2ndproject.attendancemanagementsystem.domain.member.repository;

import com.sesac2ndproject.attendancemanagementsystem.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 로그인 id로 회원 정보 찾기
    Optional<Member> findByLoginId(String loginId);

    // 중복 id 방지
    boolean existsByLoginId(String loginId);
}
