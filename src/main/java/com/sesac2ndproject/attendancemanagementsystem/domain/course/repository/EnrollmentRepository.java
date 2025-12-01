package com.sesac2ndproject.attendancemanagementsystem.domain.course.repository;

import com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto.DailyAttendanceDTO;
import com.sesac2ndproject.attendancemanagementsystem.domain.course.entity.Enrollment;
import com.sesac2ndproject.attendancemanagementsystem.global.type.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Team D가 사용할 쿼리 예시 1:
    // "특정 강의(courseId)를 듣고 있는(ACTIVE) 학생들의 ID만 다 내놔"
    @Query("SELECT e.memberId FROM Enrollment e WHERE e.courseId = :courseId AND e.status = :status")
    List<Long> findMemberIdsByCourseIdAndStatus(@Param("courseId") Long courseId, @Param("status") EnrollmentStatus status);

    // Team D 쿼리 1 : 해당하는 courseId를 가지고 Member들만 찾아오기.
    List<Enrollment> findMemberIdByCourseId(Long courseId);

    // Team D 쿼리 2 : *통합 출석부 조회:** 날짜 + 과정ID를 받으면 → 해당 수강생들의 `DailyAttendance`와 `DetailedAttendance`를 조인(또는 Fetch)하여 가져오기.
    @Query(
            "SELECT new com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto.DailyAttendanceDTO.ResponseByDateAndCourseIdDTO(" +
            "da.dailyAttendanceId, da.memberId, er.courseId, da.date, da.finalStatus, " +
            "dea.sessionType, dea.checkTime, dea.inputNumber) " +
            " FROM DailyAttendance AS da" +
            " JOIN Enrollment AS er ON da.memberId = er.memberId" +
            " JOIN DetailedAttendance AS dea ON da.memberId = dea.memberId" +
            " WHERE da.date = :workDate AND er.courseId = :courseId")
    List<DailyAttendanceDTO.ResponseByDateAndCourseIdDTO> integratedAttendance(@Param("workDate") LocalDate workDate, @Param("courseId") Long courseId);
}
