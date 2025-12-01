package com.sesac2ndproject.attendancemanagementsystem.domain.course.repository;


import com.sesac2ndproject.attendancemanagementsystem.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
