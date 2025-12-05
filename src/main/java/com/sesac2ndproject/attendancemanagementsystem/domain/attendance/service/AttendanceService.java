package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.service;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.response.AttendanceCheckResponse;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.event.AttendanceLogCreatedEvent;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.AttendanceConfig;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DailyAttendance;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DetailedAttendance;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.AttendanceConfigRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.DailyAttendanceRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.DetailedAttendanceRepository;
import com.sesac2ndproject.attendancemanagementsystem.global.error.exception.AttendanceConfigNotFoundException;
import com.sesac2ndproject.attendancemanagementsystem.global.error.exception.AttendanceTimeExpiredException;
import com.sesac2ndproject.attendancemanagementsystem.global.error.exception.DuplicateAttendanceException;
import com.sesac2ndproject.attendancemanagementsystem.global.error.exception.InvalidAuthNumberException;
import com.sesac2ndproject.attendancemanagementsystem.global.error.exception.AttendanceException;
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

/**
 * Person 1: 출석 입력 및 검증 서비스 (Event 기반)
 * 
 * ✅ 통합 기능:
 * - 시간 기반 자동 타입 판단 (MORNING, LUNCH, DINNER)
 * - 출석/지각 자동 상태 판단
 * - DailyAttendance 시간대별 상태 관리
 * - 커스텀 예외 처리
 * - IP 검증 (껍데기)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {

    private final AttendanceConfigRepository configRepository;
    private final DetailedAttendanceRepository detailedAttendanceRepository;
    private final DailyAttendanceRepository dailyAttendanceRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 출석 체크 메인 메서드
     */
    @Transactional
    public AttendanceCheckResponse checkAttendance(
            Long memberId,
            Long courseId,
            AttendanceType type,
            String inputNumber,
            String connectionIp
    ) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        log.info("📝 출석 체크 시작 - memberId: {}, courseId: {}, type: {}, time: {}",
                memberId, courseId, type, now);

        try {
            // 1. 출석 설정 조회
            AttendanceConfig config = findAttendanceConfig(courseId, today, type);

            // 2. 중복 체크
            validateDuplicateAttendance(memberId, courseId, type, today);

            // 3. 인증번호 검증
            validateAuthNumber(inputNumber, config.getAuthNumber());

            // 4. 시간 검증
            validateTime(currentTime, config);

            // 5. IP 검증 (껍데기 - 일단 true 반환)
            validateIpAddress(connectionIp);

            // 6. 출석 상태 판단 (출석/지각)
            AttendanceStatus attendanceStatus = determineStatus(type, currentTime, config);
            log.info("📌 출석 상태 판단: {}", attendanceStatus);

            // 7. 성공 기록 저장
            DetailedAttendance successRecord = saveSuccessRecord(
                    memberId, courseId, type, inputNumber, now, connectionIp
            );

            // 8. DailyAttendance 업데이트 (시간대별 상태)
            updateDailyAttendance(memberId, courseId, today, type, attendanceStatus);

            // 9. 이벤트 발행
            publishAttendanceEvent(successRecord, today);

            log.info("✅ 출석 체크 성공 - detailedAttendanceId: {}, memberId: {}, status: {}",
                    successRecord.getId(), memberId, attendanceStatus);

            String statusMessage = attendanceStatus == AttendanceStatus.LATE
                    ? "지각 처리되었습니다."
                    : "출석이 정상적으로 처리되었습니다.";

            return AttendanceCheckResponse.success(statusMessage, now);

        } catch (AttendanceException e) {
            // 커스텀 예외 처리
            log.warn("❌ 출석 체크 실패 - memberId: {}, reason: {}", memberId, e.getMessage());

            DetailedAttendance failureRecord = saveFailureRecord(
                    memberId, courseId, type, inputNumber, now, connectionIp, e.getMessage()
            );

            publishAttendanceEvent(failureRecord, today);

            return AttendanceCheckResponse.failure(e.getMessage(), now);

        } catch (IllegalArgumentException | IllegalStateException e) {
            // 기존 예외 호환 (레거시)
            log.warn("❌ 출석 체크 실패 - memberId: {}, reason: {}", memberId, e.getMessage());

            DetailedAttendance failureRecord = saveFailureRecord(
                    memberId, courseId, type, inputNumber, now, connectionIp, e.getMessage()
            );

            publishAttendanceEvent(failureRecord, today);

            return AttendanceCheckResponse.failure(e.getMessage(), now);
        }
    }

    /**
     * 출석 체크 (자동 타입 판단 버전)
     * 시간에 따라 MORNING, LUNCH, DINNER를 자동으로 판단합니다.
     */
    @Transactional
    public AttendanceCheckResponse checkAttendanceAuto(
            Long memberId,
            Long courseId,
            String inputNumber,
            String connectionIp
    ) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        log.info("📝 자동 출석 체크 시작 - memberId: {}, courseId: {}, time: {}",
                memberId, courseId, now);

        try {
            // 1. 현재 시간으로 출석 타입 자동 판단
            AttendanceType autoType = determineType(currentTime, courseId, today);

            if (autoType == null) {
                throw new IllegalArgumentException("현재는 출석 가능 시간이 아닙니다.");
            }

            log.info("📌 자동 판단된 출석 타입: {}", autoType);

            // 2. 기존 로직 호출
            return checkAttendance(memberId, courseId, autoType, inputNumber, connectionIp);

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("❌ 자동 출석 체크 실패 - memberId: {}, reason: {}", memberId, e.getMessage());
            return AttendanceCheckResponse.failure(e.getMessage(), now);
        }
    }

    /**
     * 시간 기반 출석 타입 자동 판단
     * 각 시간대의 설정을 조회하여 현재 시간이 속하는 타입을 반환합니다.
     */
    private AttendanceType determineType(LocalTime time, Long courseId, LocalDate today) {
        // 모든 시간대 설정 조회
        List<AttendanceConfig> configs = configRepository.findAll().stream()
                .filter(c -> c.getCourseId().equals(courseId) && c.getTargetDate().equals(today))
                .toList();

        for (AttendanceConfig config : configs) {
            LocalTime baseTime = config.getStandardTime();
            int validMinutes = config.getValidMinutes() != null ? config.getValidMinutes() : 20;

            LocalTime startTime = baseTime.minusMinutes(validMinutes);
            LocalTime endTime = config.getDeadline();

            if (!time.isBefore(startTime) && !time.isAfter(endTime)) {
                return config.getType();
            }
        }

        // 설정이 없는 경우 기본 시간대로 판단 (fallback)
        return determineTypeByDefaultRange(time);
    }

    /**
     * 기본 시간 범위로 타입 판단 (설정이 없을 때 fallback)
     */
    private AttendanceType determineTypeByDefaultRange(LocalTime time) {
        // MORNING: 08:40 ~ 09:20
        if (!time.isBefore(LocalTime.of(8, 40)) && !time.isAfter(LocalTime.of(9, 20))) {
            return AttendanceType.MORNING;
        }

        // LUNCH: 12:10 ~ 12:50
        if (!time.isBefore(LocalTime.of(12, 10)) && !time.isAfter(LocalTime.of(12, 50))) {
            return AttendanceType.LUNCH;
        }

        // DINNER: 17:30 ~ 18:10
        if (!time.isBefore(LocalTime.of(17, 30)) && !time.isAfter(LocalTime.of(18, 10))) {
            return AttendanceType.DINNER;
        }

        return null;
    }

    /**
     * 출석 상태 판단 (PRESENT / LATE)
     * 기준 시간 이전이면 출석, 이후면 지각
     */
    private AttendanceStatus determineStatus(AttendanceType type, LocalTime requestTime, AttendanceConfig config) {
        LocalTime limitTime = config.getStandardTime();

        if (requestTime.isAfter(limitTime)) {
            return AttendanceStatus.LATE;
        } else {
            return AttendanceStatus.PRESENT;
        }
    }

    /**
     * DailyAttendance 업데이트
     * 해당 학생의 일별 출석 현황에서 시간대별 상태를 업데이트합니다.
     */
    private void updateDailyAttendance(
            Long memberId,
            Long courseId,
            LocalDate date,
            AttendanceType type,
            AttendanceStatus status
    ) {
        DailyAttendance dailyAttendance = dailyAttendanceRepository
                .findByMemberIdAndCourseIdAndDate(memberId, courseId, date)
                .orElseGet(() -> {
                    log.info("📌 새로운 DailyAttendance 생성 - memberId: {}, courseId: {}, date: {}",
                            memberId, courseId, date);
                    return DailyAttendance.builder()
                            .memberId(memberId)
                            .courseId(courseId)
                            .date(date)
                            .build();
                });

        dailyAttendance.markPeriod(type, status);
        dailyAttendanceRepository.save(dailyAttendance);

        log.info("📌 DailyAttendance 업데이트 완료 - type: {}, status: {}", type, status);
    }

    /**
     * 이벤트 발행
     */
    private void publishAttendanceEvent(DetailedAttendance record, LocalDate targetDate) {
        AttendanceLogCreatedEvent event = new AttendanceLogCreatedEvent(
                record.getMemberId(),
                record.getCourseId(),
                targetDate,
                record.getType(),
                record.isVerified(),
                record.getId()
        );

        log.info("📢 이벤트 발행 - {}", event);
        eventPublisher.publishEvent(event);
    }

    /**
     * 출석 설정 조회
     */
    private AttendanceConfig findAttendanceConfig(
            Long courseId,
            LocalDate targetDate,
            AttendanceType type
    ) {
        return configRepository.findByCourseIdAndTargetDateAndType(courseId, targetDate, type)
                .orElseThrow(() -> new AttendanceConfigNotFoundException(type));
    }

    /**
     * 중복 출석 검증
     */
    private void validateDuplicateAttendance(
            Long memberId,
            Long courseId,
            AttendanceType type,
            LocalDate today
    ) {
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        boolean alreadyChecked = detailedAttendanceRepository.existsVerifiedAttendanceToday(
                memberId,
                courseId,
                type,
                startOfDay,
                endOfDay
        );

        if (alreadyChecked) {
            throw new DuplicateAttendanceException(type);
        }
    }

    /**
     * 인증번호 검증
     */
    private void validateAuthNumber(String inputNumber, String correctNumber) {
        if (inputNumber == null || inputNumber.trim().isEmpty()) {
            throw new InvalidAuthNumberException("인증번호를 입력해주세요.");
        }

        if (!inputNumber.equals(correctNumber)) {
            throw new InvalidAuthNumberException();
        }
    }

    /**
     * 시간 검증
     */
    private void validateTime(LocalTime currentTime, AttendanceConfig config) {
        LocalTime startTime = config.getStandardTime();
        LocalTime endTime = config.getDeadline();

        if (currentTime.isBefore(startTime)) {
            throw new AttendanceTimeExpiredException(startTime, endTime);
        }

        if (currentTime.isAfter(endTime)) {
            throw new AttendanceTimeExpiredException(startTime, endTime);
        }
    }

    /**
     * IP 주소 검증 (껍데기 - 추후 구현 예정)
     * 현재는 항상 true를 반환합니다.
     * 
     * TODO: 실제 IP 검증 로직 구현
     * - 허용된 IP 목록과 비교
     * - VPN/프록시 감지
     * - 지역 기반 검증 등
     * 
     * @param connectionIp 접속 IP 주소
     * @return 항상 true (추후 구현 시 false 반환 가능)
     */
    private boolean validateIpAddress(String connectionIp) {
        log.info("📌 IP 검증 시작 - IP: {}", connectionIp);
        
        // TODO: 실제 IP 검증 로직 구현
        // 예시:
        // List<String> allowedIps = ipConfigRepository.findAllowedIps();
        // if (!allowedIps.contains(connectionIp)) {
        //     throw new InvalidIpAddressException(connectionIp);
        // }
        
        // 현재는 껍데기로 항상 true 반환
        log.info("📌 IP 검증 완료 - IP: {} (검증 통과)", connectionIp);
        return true;
    }

    /**
     * 성공 기록 저장
     */
    private DetailedAttendance saveSuccessRecord(
            Long memberId,
            Long courseId,
            AttendanceType type,
            String inputNumber,
            LocalDateTime checkTime,
            String connectionIp
    ) {
        DetailedAttendance record = DetailedAttendance.builder()
                .memberId(memberId)
                .courseId(courseId)
                .dailyAttendanceId(null)
                .type(type)
                .inputNumber(inputNumber)
                .checkTime(checkTime)
                .connectionIp(connectionIp)
                .isVerified(true)
                .failReason(null)
                .build();

        return detailedAttendanceRepository.save(record);
    }

    /**
     * 실패 기록 저장
     */
    private DetailedAttendance saveFailureRecord(
            Long memberId,
            Long courseId,
            AttendanceType type,
            String inputNumber,
            LocalDateTime checkTime,
            String connectionIp,
            String failReason
    ) {
        DetailedAttendance record = DetailedAttendance.builder()
                .memberId(memberId)
                .courseId(courseId)
                .dailyAttendanceId(null)
                .type(type)
                .inputNumber(inputNumber)
                .checkTime(checkTime)
                .connectionIp(connectionIp)
                .isVerified(false)
                .failReason(failReason)
                .build();

        return detailedAttendanceRepository.save(record);
    }
}