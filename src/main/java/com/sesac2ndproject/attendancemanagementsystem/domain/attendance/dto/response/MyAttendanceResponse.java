package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.entity.DailyAttendance;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * 내 출석 조회 응답
 */
@Getter
@Builder
public class MyAttendanceResponse {

    private Long memberId;
    private Long courseId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    // 전체 상태
    private AttendanceStatus overallStatus;
    private String overallStatusDescription;

    // 시간대별 상태
    private AttendanceStatus morningStatus;
    private AttendanceStatus lunchStatus;
    private AttendanceStatus dinnerStatus;

    // 상세 기록
    private List<AttendanceDetailResponse> details;

    // DailyAttendance로 응답 생성
    public static MyAttendanceResponse of(DailyAttendance daily, AttendanceStatus overallStatus, List<AttendanceDetailResponse> details) {
        return MyAttendanceResponse.builder()
                .memberId(daily.getMemberId())
                .courseId(daily.getCourseId())
                .date(daily.getDate())
                .overallStatus(overallStatus)
                .overallStatusDescription(overallStatus != null ? overallStatus.getDescription() : "미확정")
                .morningStatus(daily.getMorningStatus())
                .lunchStatus(daily.getLunchStatus())
                .dinnerStatus(daily.getDinnerStatus())
                .details(details)
                .build();
    }

    // 출석 기록이 없는 경우
    public static MyAttendanceResponse empty(Long memberId, Long courseId, LocalDate date) {
        return MyAttendanceResponse.builder()
                .memberId(memberId)
                .courseId(courseId)
                .date(date)
                .overallStatus(AttendanceStatus.NONE)
                .overallStatusDescription("출석 기록 없음")
                .morningStatus(AttendanceStatus.NONE)
                .lunchStatus(AttendanceStatus.NONE)
                .dinnerStatus(AttendanceStatus.NONE)
                .details(List.of())
                .build();
    }
}

