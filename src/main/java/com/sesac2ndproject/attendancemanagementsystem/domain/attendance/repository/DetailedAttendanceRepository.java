package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DetailedAttendance;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * DetailedAttendance Repository
 */
public interface DetailedAttendanceRepository extends JpaRepository<DetailedAttendance, Long> {

    /**
     * 오늘 해당 세션에 이미 성공한 출석이 있는지 확인
     *
     * ✅ 방법 1: 시간 범위로 검색 (가장 안전)
     *
     * @param memberId 회원 ID
     * @param courseId 강의 ID
     * @param type 출석 타입 (MORNING, LUNCH, DINNER)
     * @param startOfDay 오늘 00:00:00
     * @param endOfDay 오늘 23:59:59
     * @return 이미 출석했으면 true
     */
    @Query("""
        SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END
        FROM DetailedAttendance d
        WHERE d.memberId = :memberId
        AND d.courseId = :courseId
        AND d.type = :type
        AND d.isVerified = true
        AND d.checkTime >= :startOfDay
        AND d.checkTime <= :endOfDay
        """)
    boolean existsVerifiedAttendanceToday(
            @Param("memberId") Long memberId,
            @Param("courseId") Long courseId,
            @Param("type") AttendanceType type,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}