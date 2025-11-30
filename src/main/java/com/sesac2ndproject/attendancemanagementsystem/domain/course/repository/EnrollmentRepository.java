package com.sesac2ndproject.attendancemanagementsystem.domain.course.repository;

import com.sesac2ndproject.attendancemanagementsystem.domain.course.entity.Enrollment;
import com.sesac2ndproject.attendancemanagementsystem.global.type.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Team D가 사용할 쿼리 예시 1:
    // "특정 강의(courseId)를 듣고 있는(ACTIVE) 학생들의 ID만 다 내놔"
    @Query("SELECT e.memberId FROM Enrollment e WHERE e.courseId = :courseId AND e.status = :status")
    List<Enrollment> findMemberIdsByCourseIdAndStatus(@Param("courseId") Long courseId, @Param("status") EnrollmentStatus status);
    // Team D 쿼리 1 : 해당하는 courseId를 가지고 Member들만 찾아오기.
    List<Enrollment> findMemberIdByCourseId(Long courseId);
}
