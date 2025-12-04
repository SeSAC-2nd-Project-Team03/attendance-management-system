package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DetailedAttendance;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 출석 상세 기록 Repository
 */
public interface DetailedAttendanceRepository extends JpaRepository<DetailedAttendance, Long> {

    /**
     * 중복 출석 체크 - 오늘 이미 출석했는지 확인
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

    /**
     * 특정 날짜의 모든 출석 기록 조회
     */
    @Query("""
        SELECT d FROM DetailedAttendance d
        WHERE d.memberId = :memberId
        AND d.courseId = :courseId
        AND d.checkTime >= :startOfDay
        AND d.checkTime <= :endOfDay
        ORDER BY d.checkTime ASC
        """)
    List<DetailedAttendance> findByDate(
            @Param("memberId") Long memberId,
            @Param("courseId") Long courseId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}