package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DailyAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyAttendanceRepository extends JpaRepository<DailyAttendance, Long> {

    // 쿼리: "특정 날짜(date)에, 이 학생들(memberIds)의 출석부 다 가져와"
    // (Team D가 Enrollment에서 학생 ID 목록을 먼저 구해오면, 여기서 그 ID들로 조회함)
    List<DailyAttendance> findByDateAndMemberIdIn(LocalDate date, List<Long> memberIds);
}
