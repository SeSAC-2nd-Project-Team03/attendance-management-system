package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.service;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.response.AttendanceCheckResponse;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.event.AttendanceLogCreatedEvent;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.AttendanceConfig;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DetailedAttendance;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.AttendanceConfigRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.DetailedAttendanceRepository;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Person 1: 출석 입력 및 검증 서비스 (Event 기반)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {

    private final AttendanceConfigRepository configRepository;
    private final DetailedAttendanceRepository detailedAttendanceRepository;
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

            // 5. 성공 기록 저장
            DetailedAttendance successRecord = saveSuccessRecord(
                    memberId, courseId, type, inputNumber, now, connectionIp
            );

            // 6. 이벤트 발행
            publishAttendanceEvent(successRecord, today);

            log.info("✅ 출석 체크 성공 - detailedAttendanceId: {}, memberId: {}",
                    successRecord.getId(), memberId);

            return AttendanceCheckResponse.success(
                    "출석이 정상적으로 처리되었습니다.", now
            );

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("❌ 출석 체크 실패 - memberId: {}, reason: {}", memberId, e.getMessage());

            DetailedAttendance failureRecord = saveFailureRecord(
                    memberId, courseId, type, inputNumber, now, connectionIp, e.getMessage()
            );

            publishAttendanceEvent(failureRecord, today);

            return AttendanceCheckResponse.failure(e.getMessage(), now);
        }
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
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("오늘 %s 출석 설정이 존재하지 않습니다.", type)
                ));
    }

    /**
     * 중복 출석 검증
     *
     * ✅ 수정: 날짜 범위 파라미터 추가
     */
    private void validateDuplicateAttendance(
            Long memberId,
            Long courseId,
            AttendanceType type,
            LocalDate today
    ) {
        // 오늘 00:00:00 ~ 23:59:59 범위
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
            throw new IllegalStateException(
                    String.format("이미 %s 출석을 완료하셨습니다.", type)
            );
        }
    }

    /**
     * 인증번호 검증
     */
    private void validateAuthNumber(String inputNumber, String correctNumber) {
        if (inputNumber == null || inputNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("인증번호를 입력해주세요.");
        }

        if (!inputNumber.equals(correctNumber)) {
            throw new IllegalArgumentException("인증번호가 올바르지 않습니다.");
        }
    }

    /**
     * 시간 검증
     */
    private void validateTime(LocalTime currentTime, AttendanceConfig config) {
        LocalTime startTime = config.getStandardTime();
        LocalTime endTime = config.getDeadlineTime();

        if (currentTime.isBefore(startTime)) {
            throw new IllegalArgumentException(
                    String.format("출석 가능 시간이 아닙니다. (출석 가능: %s ~ %s)",
                            startTime, endTime)
            );
        }

        if (currentTime.isAfter(endTime)) {
            throw new IllegalArgumentException(
                    String.format("출석 가능 시간이 아닙니다. (출석 가능: %s ~ %s)",
                            startTime, endTime)
            );
        }
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