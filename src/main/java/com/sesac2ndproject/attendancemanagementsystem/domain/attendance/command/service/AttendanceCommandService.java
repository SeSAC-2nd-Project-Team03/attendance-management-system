package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.command.service;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.configure.entity.AttendanceConfig;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.configure.repository.AttendanceConfigRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.event.AttendanceLogCreatedEvent;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.response.AttendanceCheckResponse;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DailyAttendance;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DetailedAttendance;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.DailyAttendanceRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.DetailedAttendanceRepository;
import com.sesac2ndproject.attendancemanagementsystem.global.error.exception.*;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceStatus;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceCommandService {

    private final AttendanceConfigRepository configRepository;
    private final DetailedAttendanceRepository detailedAttendanceRepository;
    private final DailyAttendanceRepository dailyAttendanceRepository;
    private final ApplicationEventPublisher eventPublisher;

    // 출석 체크
    public AttendanceCheckResponse checkAttendance(
            Long memberId, Long courseId, AttendanceType type, String inputNumber, String connectionIp
    ) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        log.info("📝 출석 체크 시작 - memberId: {}, type: {}", memberId, type);

        try {
            // 1. 검증 로직 (설정, 중복, 인증번호, 시간, IP)
            AttendanceConfig config = findAttendanceConfig(courseId, today, type);
            validateDuplicateAttendance(memberId, courseId, type, today);
            validateAuthNumber(inputNumber, config.getAuthNumber());
            validateTime(currentTime, config);
            validateIpAddress(connectionIp);

            // 2. 상태 판단 (PRESENT or LATE)
            AttendanceStatus status = (currentTime.isAfter(config.getStandardTime()))
                    ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;

            // 3. 기록 저장 (Success Log)
            DetailedAttendance successRecord = saveRecord(memberId, courseId, type, inputNumber, now, connectionIp, true, null);

            // 4. Daily 상태 업데이트
            updateDailyAttendance(memberId, courseId, today, type, status);

            // 5. 이벤트 발행
            publishEvent(successRecord, today);

            return AttendanceCheckResponse.success(
                    status == AttendanceStatus.LATE ? "지각 처리되었습니다." : "출석이 정상적으로 처리되었습니다.", now
            );

        } catch (AttendanceException | IllegalArgumentException e) {
            log.warn("❌ 출석 체크 실패 - reason: {}", e.getMessage());
            // 실패 기록 저장 (Failure Log)
            DetailedAttendance failRecord = saveRecord(memberId, courseId, type, inputNumber, now, connectionIp, false, e.getMessage());
            publishEvent(failRecord, today);

            return AttendanceCheckResponse.failure(e.getMessage(), now);
        }
    }

    // 자동 출석 로직 - 현재 시간을 기준으로 어떤 출석인지 자동으로 찾아줌
    public AttendanceCheckResponse checkAttendanceAuto(
            Long memberId, Long courseId, String inputNumber, String connectionIp
    ) {
        LocalDateTime now = LocalDateTime.now();
        AttendanceType autoType = determineType(now.toLocalTime(), courseId, now.toLocalDate());

        if (autoType == null) {
            return AttendanceCheckResponse.failure("현재는 출석 가능 시간이 아닙니다.", now);
        }
        return checkAttendance(memberId, courseId, autoType, inputNumber, connectionIp);
    }

    // 미체크 결석 처리 - 하루가 끝날 때 아직 출석 체크를 안 한 학생들을 일괄적으로 결석 처리함
    public int markAbsentForUnmarked(Long courseId, LocalDate date, List<Long> memberIds) {
        log.info("SYSTEM: 미체크 결석 처리 시작 - date: {}", date);
        int count = 0;

        for (Long memberId : memberIds) {
            DailyAttendance daily = dailyAttendanceRepository
                    .findByMemberIdAndCourseIdAndDate(memberId, courseId, date)
                    .orElse(null);

            if (daily == null) {
                // 아예 기록이 없으면 결석 데이터 생성
                DailyAttendance absentDaily = DailyAttendance.builder()
                        .memberId(memberId)
                        .courseId(courseId)
                        .date(date)
                        .morningStatus(AttendanceStatus.ABSENT)
                        .lunchStatus(AttendanceStatus.ABSENT)
                        .dinnerStatus(AttendanceStatus.ABSENT)
                        .build();
                dailyAttendanceRepository.save(absentDaily);
                count++;
            }
        }
        return count;
    }

    // ================== Private Helpers ==================

    private AttendanceConfig findAttendanceConfig(Long courseId, LocalDate date, AttendanceType type) {
        return configRepository.findByCourseIdAndTargetDateAndType(courseId, date, type)
                .orElseThrow(() -> new AttendanceConfigNotFoundException(type));
    }

    private void validateDuplicateAttendance(Long memberId, Long courseId, AttendanceType type, LocalDate date) {
        boolean exists = detailedAttendanceRepository.existsVerifiedAttendanceToday(
                memberId, courseId, type, date.atStartOfDay(), date.atTime(23, 59, 59)
        );
        if (exists) throw new DuplicateAttendanceException(type);
    }

    private void validateAuthNumber(String input, String correct) {
        if (input == null || !input.equals(correct)) throw new InvalidAuthNumberException();
    }

    private void validateTime(LocalTime current, AttendanceConfig config) {
        // 유효 시간(validMinutes) 고려하여 시작 시간 계산
        int validMinutes = config.getValidMinutes() != null ? config.getValidMinutes() : 20;
        LocalTime startTime = config.getStandardTime().minusMinutes(validMinutes);

        if (current.isBefore(startTime) || current.isAfter(config.getDeadline())) {
            throw new AttendanceTimeExpiredException(startTime, config.getDeadline());
        }
    }

    private void validateIpAddress(String ip) {
        // TODO: IP 검증 로직 구현
    }

    private DetailedAttendance saveRecord(Long memberId, Long courseId, AttendanceType type, String input, LocalDateTime time, String ip, boolean verified, String failReason) {
        return detailedAttendanceRepository.save(DetailedAttendance.builder()
                .memberId(memberId).courseId(courseId).type(type).inputNumber(input)
                .checkTime(time).connectionIp(ip).isVerified(verified).failReason(failReason)
                .build());
    }

    private void updateDailyAttendance(Long memberId, Long courseId, LocalDate date, AttendanceType type, AttendanceStatus status) {
        DailyAttendance daily = dailyAttendanceRepository.findByMemberIdAndCourseIdAndDate(memberId, courseId, date)
                .orElseGet(() -> DailyAttendance.builder().memberId(memberId).courseId(courseId).date(date).build());
        daily.markPeriod(type, status);
        dailyAttendanceRepository.save(daily);
    }

    private void publishEvent(DetailedAttendance record, LocalDate date) {
        eventPublisher.publishEvent(new AttendanceLogCreatedEvent(
                record.getMemberId(), record.getCourseId(), date, record.getType(), record.isVerified(), record.getId()
        ));
    }

    private AttendanceType determineType(LocalTime time, Long courseId, LocalDate date) {
        // DB 설정 기반 판단 로직 (간소화)
        List<AttendanceConfig> configs = configRepository.findAll().stream()
                .filter(c -> c.getCourseId().equals(courseId) && c.getTargetDate().equals(date)).toList();

        for (AttendanceConfig c : configs) {
            int validMin = c.getValidMinutes() != null ? c.getValidMinutes() : 20;
            if (!time.isBefore(c.getStandardTime().minusMinutes(validMin)) && !time.isAfter(c.getDeadline())) {
                return c.getType();
            }
        }
        return null; // or default logic
    }
}