package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.batch.service;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.common.entity.DailyAttendance;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.common.repository.DailyAttendanceRepository;
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
public class AttendanceSchedulerService {

    private final DailyAttendanceRepository dailyAttendanceRepository;

    /**
     * [스케줄러용] 특정 회원의 오늘 출석 상태 계산
     * (DailyAttendance의 morning/lunch/dinner 상태를 조합하여 전체 상태 도출)
     */
    @Transactional(readOnly = true)
    public AttendanceStatus calculateMemberDailyStatus(Long memberId, Long courseId, LocalDate date) {
        // 로그가 너무 많으면 성능 저하되므로 debug 레벨 권장, 일단은 info 유지
        log.info("SCHEDULER: 회원 상태 계산 - memberId: {}, date: {}", memberId, date);

        DailyAttendance daily = dailyAttendanceRepository
                .findByMemberIdAndCourseIdAndDate(memberId, courseId, date)
                .orElse(null);

        if (daily == null) {
            return AttendanceStatus.NONE;
        }

        // AttendanceRule을 사용하여 최종 상태 계산
        return AttendanceRule.calculate(
                daily.getMorningStatus(),
                daily.getLunchStatus(),
                daily.getDinnerStatus()
        );
    }

    /**
     * [스케줄러용] 전체 회원 상태 일괄 계산
     * (보통 밤 12시나 특정 시점에 배치로 돌리는 로직)
     */
    @Transactional(readOnly = true)
    public int calculateAllMembersDailyStatus(Long courseId, LocalDate date, List<Long> memberIds) {
        log.info("SCHEDULER: 일괄 상태 계산 시작 - members: {}명", memberIds.size());

        int count = 0;
        for (Long memberId : memberIds) {
            AttendanceStatus status = calculateMemberDailyStatus(memberId, courseId, date);
            if (status != AttendanceStatus.NONE) {
                count++;
            }
        }
        log.info("SCHEDULER: 일괄 상태 계산 완료 - 처리: {}명", count);
        return count;
    }
}
