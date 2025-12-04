package com.sesac2ndproject.attendancemanagementsystem.domain.course.repository;

import com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto.DailyAttendanceDTO;
import com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto.ResponseAttendanceByDateDTO;
import com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto.ResponseByDateAndCourseIdDTO;
import com.sesac2ndproject.attendancemanagementsystem.domain.course.entity.Enrollment;
import com.sesac2ndproject.attendancemanagementsystem.global.type.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {


    // Team D 쿼리 1 : 해당하는 courseId를 가지고 Member들만 찾아오기.
    List<Enrollment> findMemberIdByCourseId(Long courseId);

    // Team D 쿼리 2 : *통합 출석부 조회:** 날짜 + 과정ID를 받으면 → 해당 수강생들의 `DailyAttendance`와 `DetailedAttendance`를 조인(또는 Fetch)하여 가져오기.
    @Query("SELECT new com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto.ResponseByDateAndCourseIdDTO(" +
            " da.id, da.memberId, enr.course.id, da.date, da.status, dea)" +
            " FROM DailyAttendance AS da" +
            " JOIN Enrollment AS enr ON da.memberId = enr.member.id" +
            " LEFT JOIN DetailedAttendance AS dea ON da.memberId = dea.memberId" +  /* 상세 기록이 없어도 가져오도록 LEFT JOIN으로 처리. 단 NULL값 주의 */
            " WHERE da.date = :workDate AND enr.course = :courseId")
    List<ResponseByDateAndCourseIdDTO> integratedAttendance(@Param("workDate") LocalDate workDate, @Param("courseId") Long courseId);

    // Team D 쿼리 3 :
//    @Query("SELECT da, mem.name, cor.id, cor.courseName, enr.status" +
//            " FROM DailyAttendance da" +
//            " JOIN Member mem ON da.memberId= mem.id" +
//            " JOIN Enrollment enr ON da.memberId = enr.member.id" +
//            " JOIN Course cor ON enr.id = cor.id" +
//            " WHERE da.date = :date AND :date >= cor.startDate AND :date <= cor.endDate")
//    List<ResponseAttendanceByDateDTO> attendanceListByDate(@Param("date") LocalDate date);
    @Query("SELECT new com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto.ResponseAttendanceByDateDTO$FlatResponse(" +
            "da.id, da.memberId, er.course.id, da.date, da.status, dea) " +
            " FROM DailyAttendance da" +
            // Daily(Long) <=> Enrollment(Member객체) 연결: er.member.memberId 사용
            " JOIN Enrollment er ON da.memberId = er.member.id" +
            // Daily <=> Detailed 연결: attendanceId 사용 (LEFT JOIN 권장: 상세기록 없어도 출석부는 나오게)
            " LEFT JOIN DetailedAttendance dea ON da.id = dea.dailyAttendanceId" +
            // Enrollment(Course객체) <=> 파라미터(Long) 비교: er.course.courseId 사용
            " WHERE da.date = :workDate AND er.course.id = :courseId")
    List<ResponseAttendanceByDateDTO.FlatResponse> findIntegratedAttendanceFlat(@Param("workDate") LocalDate workDate, @Param("courseId") Long courseId);


    @Query("SELECT e.member.id FROM Enrollment e WHERE e.course.id = :courseId AND e.status = :status")
    List<Long> findMemberIdsByCourseIdAndStatus(
            @Param("courseId") Long courseId,
            @Param("status") EnrollmentStatus status
    );
}
