package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DetailedAttendance;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DetailedAttendanceRepository extends JpaRepository<DetailedAttendance, Long> {


    List<DetailedAttendance> findAllByMemberIdAndCheckTimeBetween(Long memberId, LocalDateTime start, LocalDateTime end);


    boolean existsByMemberIdAndTypeAndCheckTimeBetween(Long memberId, AttendanceType type, LocalDateTime start, LocalDateTime end);


    Optional<DetailedAttendance> findByMemberIdAndTypeAndCheckTimeBetween(Long memberId, AttendanceType type, LocalDateTime start, LocalDateTime end);
}