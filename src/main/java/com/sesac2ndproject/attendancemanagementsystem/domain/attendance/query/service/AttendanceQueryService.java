package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.query.service;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.common.dto.response.AttendanceDetailResponse;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.query.dto.MyAttendanceResponse;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.common.entity.DailyAttendance;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.common.entity.DetailedAttendance;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.common.repository.DailyAttendanceRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.common.repository.DetailedAttendanceRepository;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceRule;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceQueryService {

    private final DailyAttendanceRepository dailyAttendanceRepository;
    private final DetailedAttendanceRepository detailedAttendanceRepository;

    /**
     * 내 출석 조회
     */
    public MyAttendanceResponse getMyAttendance(Long memberId, Long courseId, LocalDate date) {
        log.info("🔍 출석 조회 - memberId: {}, date: {}", memberId, date);

        // 1. DailyAttendance (일일 요약) 조회
        DailyAttendance daily = dailyAttendanceRepository
                .findByMemberIdAndCourseIdAndDate(memberId, courseId, date)
                .orElse(null);

        // 2. DetailedAttendance (상세 로그) 조회
        List<DetailedAttendance> detailedList = detailedAttendanceRepository
                .findByDate(memberId, courseId, date.atStartOfDay(), date.atTime(23, 59, 59));

        List<AttendanceDetailResponse> details = detailedList.stream()
                .map(AttendanceDetailResponse::from)
                .toList();

        // 3. 응답 생성
        if (daily == null) {
            return MyAttendanceResponse.builder()
                    .memberId(memberId).courseId(courseId).date(date)
                    .overallStatus(AttendanceStatus.NONE)
                    .overallStatusDescription("출석 기록 없음")
                    .details(details)
                    .build();
        }

        AttendanceStatus overallStatus = calculateDailyStatus(daily);
        return MyAttendanceResponse.of(daily, overallStatus, details);
    }

    /**
     * 일일 출석 상태 계산 로직 (순수 로직)
     */
    public AttendanceStatus calculateDailyStatus(DailyAttendance daily) {
        if (daily == null) return AttendanceStatus.NONE;
        return AttendanceRule.calculate(
                daily.getMorningStatus(),
                daily.getLunchStatus(),
                daily.getDinnerStatus()
        );
    }
}
