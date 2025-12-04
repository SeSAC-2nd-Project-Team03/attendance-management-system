package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.service;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.response.AttendanceDetailResponse;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.response.MyAttendanceResponse;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DailyAttendance;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DetailedAttendance;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.DailyAttendanceRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.DetailedAttendanceRepository;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 출석 상태 조회 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceStatusService {

    private final DailyAttendanceRepository dailyAttendanceRepository;
    private final DetailedAttendanceRepository detailedAttendanceRepository;

    /**
     * 내 출석 조회
     */
    public MyAttendanceResponse getMyAttendance(Long memberId, Long courseId, LocalDate date) {
        log.info("출석 조회 - memberId: {}, courseId: {}, date: {}", memberId, courseId, date);

        try {
            // 1. DailyAttendance 조회
            DailyAttendance daily = dailyAttendanceRepository
                    .findByMemberIdAndCourseIdAndDate(memberId, courseId, date)
                    .orElse(null);

            // 2. DetailedAttendance 조회
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            List<DetailedAttendance> detailedList = detailedAttendanceRepository
                    .findByDate(memberId, courseId, startOfDay, endOfDay);

            // 3. 상세 기록 변환
            List<AttendanceDetailResponse> details = detailedList.stream()
                    .map(AttendanceDetailResponse::from)
                    .toList();

            // 4. DailyAttendance가 없으면 빈 응답 (상세 기록 포함)
            if (daily == null) {
                return MyAttendanceResponse.builder()
                        .memberId(memberId)
                        .courseId(courseId)
                        .date(date)
                        .overallStatus(AttendanceStatus.NONE)
                        .overallStatusDescription("출석 기록 없음")
                        .morningStatus(null)
                        .lunchStatus(null)
                        .dinnerStatus(null)
                        .details(details)
                        .build();
            }

            // 5. 전체 상태 계산
            AttendanceStatus overallStatus = calculateStatus(daily);

            return MyAttendanceResponse.of(daily, overallStatus, details);

        } catch (Exception e) {
            log.error("출석 조회 실패 - memberId: {}, error: {}", memberId, e.getMessage(), e);
            return MyAttendanceResponse.empty(memberId, courseId, date);
        }
    }

    /**
     * 전체 상태 계산
     */
    public AttendanceStatus calculateStatus(DailyAttendance daily) {
        if (daily == null) {
            return AttendanceStatus.NONE;
        }

        return AttendanceRule.calculate(
                daily.getMorningStatus(),
                daily.getLunchStatus(),
                daily.getDinnerStatus()
        );
    }

    // ==========================================
    // 스케줄러용 메서드
    // ==========================================

    /**
     * 특정 회원의 오늘 출석 상태 계산 (스케줄러/배치용)
     * 
     * DetailedAttendance 기록을 기반으로 DailyAttendance 상태 계산
     */
    @Transactional
    public AttendanceStatus calculateMemberDailyStatus(Long memberId, Long courseId, LocalDate date) {
        log.info("회원 일일 상태 계산 - memberId: {}, courseId: {}, date: {}", memberId, courseId, date);

        // DailyAttendance 조회
        DailyAttendance daily = dailyAttendanceRepository
                .findByMemberIdAndCourseIdAndDate(memberId, courseId, date)
                .orElse(null);

        if (daily == null) {
            log.info("DailyAttendance 없음 - memberId: {}", memberId);
            return AttendanceStatus.NONE;
        }

        // 상태 계산
        AttendanceStatus result = calculateStatus(daily);
        log.info("계산 결과 - memberId: {}, status: {}", memberId, result);

        return result;
    }

    /**
     * 여러 회원의 일일 상태 일괄 계산 (스케줄러용)
     * 
     * @param courseId 강의 ID
     * @param date 날짜
     * @param memberIds 회원 ID 목록
     * @return 처리된 회원 수
     */
    @Transactional
    public int calculateAllMembersDailyStatus(Long courseId, LocalDate date, List<Long> memberIds) {
        log.info("일괄 상태 계산 시작 - courseId: {}, date: {}, members: {}명", courseId, date, memberIds.size());

        int count = 0;
        for (Long memberId : memberIds) {
            AttendanceStatus status = calculateMemberDailyStatus(memberId, courseId, date);
            if (status != AttendanceStatus.NONE) {
                count++;
            }
        }

        log.info("일괄 상태 계산 완료 - 처리: {}명", count);
        return count;
    }

    /**
     * 하루 종료 시 미체크 학생 결석 처리 (스케줄러용)
     * 
     * 저녁 시간 이후 호출하여 미체크 상태를 결석으로 변경
     */
    @Transactional
    public int markAbsentForUnmarked(Long courseId, LocalDate date, List<Long> memberIds) {
        log.info("미체크 결석 처리 시작 - courseId: {}, date: {}", courseId, date);

        int count = 0;
        for (Long memberId : memberIds) {
            DailyAttendance daily = dailyAttendanceRepository
                    .findByMemberIdAndCourseIdAndDate(memberId, courseId, date)
                    .orElse(null);

            // 기록이 없거나 미체크 상태가 있으면 처리
            if (daily == null) {
                // 기록 자체가 없으면 결석 처리할 DailyAttendance 생성
                daily = DailyAttendance.builder()
                        .memberId(memberId)
                        .courseId(courseId)
                        .date(date)
                        .morningStatus(AttendanceStatus.ABSENT)
                        .lunchStatus(AttendanceStatus.ABSENT)
                        .dinnerStatus(AttendanceStatus.ABSENT)
                        .build();
                dailyAttendanceRepository.save(daily);
                count++;
                log.info("결석 처리 - memberId: {}", memberId);
            }
        }

        log.info("미체크 결석 처리 완료 - 처리: {}명", count);
        return count;
    }
}

