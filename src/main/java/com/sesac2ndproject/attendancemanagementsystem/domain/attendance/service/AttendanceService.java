package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.service;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.AttendanceConfigCreateRequest;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.AttendanceConfig;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.AttendanceConfigRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.DailyAttendanceRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.DetailedAttendanceRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.course.repository.CourseRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.member.repository.MemberRepository;
import com.sesac2ndproject.attendancemanagementsystem.global.error.CustomException;
import com.sesac2ndproject.attendancemanagementsystem.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceConfigRepository attendanceConfigRepository;

    @Transactional
    public Long createAttendanceConfig(AttendanceConfigCreateRequest request, Long adminId) {
        // 중복 확인
        attendanceConfigRepository.findByCourseIdAndTargetDateAndType(
                request.getCourseId(),
                request.getTargetDate(),
                request.getType()
        ).ifPresent(config -> {
            throw new CustomException(ErrorCode.DUPLICATE_ATTENDANCE_CONFIG);
        });

        // 엔티티 생성
        AttendanceConfig attendanceConfig = new AttendanceConfig(
                request.getCourseId(),
                adminId,
                request.getType(),
                request.getAuthNumber(),
                request.getTargetDate(),
                request.getStandardTime()
        );

        // 저장 및 아이디 반환
        attendanceConfigRepository.save(attendanceConfig);
        return attendanceConfig.getId();
    }



}
