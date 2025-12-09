package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.configure.repository;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.configure.entity.AttendanceConfig;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceConfigRepository extends JpaRepository<AttendanceConfig, Long> {

    // 쿼리: "특정 반(courseId)의 오늘 날짜(date) 아침/점심/저녁(type) 설정을 찾아줘"
    Optional<AttendanceConfig> findByCourseIdAndTargetDateAndType(
            Long courseId,
            LocalDate targetDate,
            AttendanceType type
    );
}
