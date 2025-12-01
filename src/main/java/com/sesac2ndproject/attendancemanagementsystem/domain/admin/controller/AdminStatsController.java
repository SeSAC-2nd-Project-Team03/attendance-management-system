package com.sesac2ndproject.attendancemanagementsystem.domain.admin.controller;

import com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto.DailyAttendanceDTO;
import com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto.ResponseByDateAndCourseIdDTO;
import com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto.StatsResponseDTO;
import com.sesac2ndproject.attendancemanagementsystem.domain.admin.service.AdminStatsService;
import com.sesac2ndproject.attendancemanagementsystem.domain.course.entity.Enrollment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminStatsController {

    private final AdminStatsService adminStatsService;


    // - **조회 로직 (Query)**
    //    - [ ]  **과정별 수강생 조회:** `Enrollment`를 통해 특정 과정(Course)을 듣는 `memberId` 목록 추출.
    @GetMapping("/enrollment")
    public ResponseEntity<List<Enrollment>> findMemberIdsByCourseId(@RequestParam Long courseId) {

        List<Enrollment> memberList = adminStatsService.findMemberIdsByCourseId(courseId);

        return ResponseEntity.ok(memberList);
    }
    //    - [ ]  **통합 출석부 조회:** 날짜 + 과정ID를 받으면 → 해당 수강생들의 `DailyAttendance`와 `DetailedAttendance`를 조인(또는 Fetch)하여 가져오기.
    @GetMapping("/attendances")
    public ResponseEntity<List<ResponseByDateAndCourseIdDTO>> findDailyAttendance(@RequestParam LocalDate date, @RequestParam Long courseId){

        List<ResponseByDateAndCourseIdDTO> dailyAttendanceList = adminStatsService.findByDateAndCourseId(date, courseId);
        return ResponseEntity.ok(dailyAttendanceList);
    }
    //- **API 개발 (관리자용)**
    //    - [ ]  **전체 출석 현황 조회 API** (`GET /api/v1/admin/attendances`): 날짜별, 과정별 전체 학생의 출석 상태 리스트 반환14
    // 날짜 -> DAILY_ATTENDANCE 반환. COURSE의 start_date와 end_date 사이에 있는 코스를 반환.
    @GetMapping("/attendances")
    public ResponseEntity<Enrollment> findDailyAttendanceByDate(@RequestParam LocalDate date) {
        return null;
    }
    //    - [ ]  **조퇴/결석 승인 처리 API** (`PATCH /api/v1/admin/leaves/{id}`): 신청 상태를 `APPROVED`로 변경15.
    //        - *(Tip: 승인 시 Team B의 `DailyAttendance` 상태를 업데이트하는 로직을 호출하거나, Team B와 협의 필요)*
    //    - [ ]  **출석 상태 강제 변경 API** (`PUT /api/v1/admin/attendances/{id}`): 시스템 판정과 상관없이 관리자가 상태(예: 지각→출석)를 직접 수정16.
    //    - [ ]  **CSV/Excel 다운로드 API**: 현재 조회된 출석부 데이터를 파일로 변환하여 응답17.
}
