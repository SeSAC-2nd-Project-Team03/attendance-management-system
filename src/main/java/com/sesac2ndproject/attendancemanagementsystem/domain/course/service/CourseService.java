package com.sesac2ndproject.attendancemanagementsystem.domain.course.service;


import com.sesac2ndproject.attendancemanagementsystem.domain.course.entity.Enrollment;
import com.sesac2ndproject.attendancemanagementsystem.domain.course.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final EnrollmentRepository enrollmentRepository;


    public List<Enrollment> findMemberIdsByCourseId(Long courseId) {
        List<Enrollment> foundEnrollment = enrollmentRepository.findMemberIdByCourseId(courseId);
        return foundEnrollment;
    }

}
