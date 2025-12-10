package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.configure.service;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.configure.dto.AttendanceConfigCreateRequest;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.configure.repository.AttendanceConfigRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.configure.entity.AttendanceConfig;
import com.sesac2ndproject.attendancemanagementsystem.global.error.CustomException;
import com.sesac2ndproject.attendancemanagementsystem.global.error.ErrorCode;
import com.sesac2ndproject.attendancemanagementsystem.global.error.exception.DuplicateAttendanceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceConfigService {

    private final AttendanceConfigRepository attendanceConfigRepository;

    // 출석 설정 생성하기
    @Transactional
    public Long createAttendanceConfig(AttendanceConfigCreateRequest request) {
        // 1. 중복 설정 검증 (이미 해당 반/날짜/타입에 설정이 있는지 확인)
        if (attendanceConfigRepository.findByCourseIdAndTargetDateAndType(
                request.getCourseId(), request.getTargetDate(), request.getType()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_ATTENDANCE_CONFIG);
        }

        // 2. 유효 시간 설정 (null이면 기본값 20분 적용)
         Integer validMinutes = (request.getValidMinutes() != null) ? request.getValidMinutes() : 20;


        // 3. 엔티티 생성 (정적 팩토리 메서드 활용 -> deadline 자동 계산됨)
        AttendanceConfig config = AttendanceConfig.create(
                request.getCourseId(),
                request.getType(),
                request.getAuthNumber(),
                request.getTargetDate(),
                request.getStandardTime(),
                validMinutes
        );

        // 4. 저장
        AttendanceConfig savedConfig = attendanceConfigRepository.save(config);
        log.info("✅ 출석 설정 완료 - ID: {}, 마감시간: {}", savedConfig.getId(), savedConfig.getDeadline());

        return savedConfig.getId();
    }
}
