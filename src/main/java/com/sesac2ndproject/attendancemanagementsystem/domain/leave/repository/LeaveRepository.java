package com.sesac2ndproject.attendancemanagementsystem.domain.leave.repository;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity.Leave;
import com.sesac2ndproject.attendancemanagementsystem.global.type.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    // 사용자의 모든 신청 조회 (삭제되지 않은 것만)
    List<Leave> findByMemberIdAndIsDeletedFalse(Long memberId);

    // 사용자의 신청 상세 조회
    Optional<Leave> findByIdAndMemberIdAndIsDeletedFalse(Long id, Long memberId);

    // 특정 날짜의 신청 조회
    List<Leave> findByMemberIdAndLeaveDateAndIsDeletedFalse(Long memberId, LocalDate leaveDate);

    // 상태별 신청 조회
    List<Leave> findByMemberIdAndStatusAndIsDeletedFalse(Long memberId, LeaveStatus status);

    // 날짜 범위로 조회
    @Query("SELECT l FROM Leave l WHERE l.memberId = :memberId " +
            "AND l.leaveDate BETWEEN :startDate AND :endDate " +
            "AND l.isDeleted = false")
    List<Leave> findByMemberIdAndDateRange(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}