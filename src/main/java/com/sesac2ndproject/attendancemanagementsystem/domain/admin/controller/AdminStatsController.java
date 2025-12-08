package com.sesac2ndproject.attendancemanagementsystem.domain.admin.controller;

import com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto.DailyAttendanceResponseDTO;
import com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto.LeaveRequestResponseDTO;
import com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto.ResponseAttendanceByDateDTO;
import com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto.ResponseByDateAndCourseIdDTO;
import com.sesac2ndproject.attendancemanagementsystem.domain.admin.service.AdminStatsService;
import com.sesac2ndproject.attendancemanagementsystem.domain.course.entity.Enrollment;
import com.sesac2ndproject.attendancemanagementsystem.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Admin", description = "수강생목록, 출석부 조회 / 수정 / 조회한 목록 다운로드")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    // - **조회 로직 (Query)**
    //    - [ ]  **과정별 수강생 조회:** `Enrollment`를 통해 특정 과정(Course)을 듣는 `memberId` 목록 추출.
    @GetMapping("/enrollment")
    @Operation(summary ="과정별 수강생 조회", description = "courseId를 입력하여 Enrollment(강좌의 수강생 목록에서 해당하는 것들을 반환")
    public ResponseEntity<ApiResponse<List<Enrollment>>> findMemberIdsByCourseId(@RequestParam Long courseId) {

        List<Enrollment> memberList = adminStatsService.findMemberIdsByCourseId(courseId);

        return ResponseEntity.ok(ApiResponse.success(memberList));
    }


    //    - [ ]  **통합 출석부 조회:** 날짜 + 과정ID를 받으면 → 해당 수강생들의 `DailyAttendance`와 `DetailedAttendance`를 조인(또는 Fetch)하여 가져오기.
    @GetMapping("/attendances")
    @Operation(summary = "통합 출석부 조회", description = "Date와 courseId를 받아서 Enrollment, DailyAttendance, DetailedAttendance 등을 조인하여 리스트를 반환.")
    public ResponseEntity<ApiResponse<List<ResponseByDateAndCourseIdDTO>>> findDailyAttendance(@RequestParam LocalDate date, @RequestParam Long courseId){

        List<ResponseByDateAndCourseIdDTO> dailyAttendanceList = adminStatsService.findByDateAndCourseId(date, courseId);
        return ResponseEntity.ok(ApiResponse.success(dailyAttendanceList));
    }


    //- **API 개발 (관리자용)**
    //    - [ ]  **전체 출석 현황 조회 API** (`GET /api/v1/admin/attendances`): 날짜별, 과정별 전체 학생의 출석 상태 리스트 반환14
    // 날짜 -> DAILY_ATTENDANCE 반환. COURSE의 start_date와 end_date 사이에 있는 코스를 반환.
    @GetMapping("/daily-attendance")
    @Operation(summary = "전체 출석 현황 조회", description = "Date와 courseId로 조회")
    public ResponseEntity<ApiResponse<List<ResponseAttendanceByDateDTO>>> getDailyAttendanceList(
            //날짜 형식을 "yyyy-MM-dd"로 받기 위해 어노테이션 사용
            @RequestParam("workDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
            @RequestParam("courseId") Long courseId
    ) {
        List<ResponseAttendanceByDateDTO> result = adminStatsService.getDailyAttendance(workDate, courseId);

        return ResponseEntity.ok(ApiResponse.success(result));
    }


    //    - [ ]  **조퇴/결석 승인 처리 API** (`PATCH /api/v1/admin/leaves/{id}`): 신청 상태를 `APPROVED`로 변경15.
    @PatchMapping("/leave/{id}")
    @Operation(summary = "조퇴/결석 신청 승인", description = "신청 상태를 APPROVED로 변경")
    public ResponseEntity<ApiResponse<LeaveRequestResponseDTO>> approveLeaveRequest(@PathVariable Long id) {
        LeaveRequestResponseDTO result = adminStatsService.requestApprove(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }


    //    - [ ]  **출석 상태 강제 변경 API** (`PUT /api/v1/admin/attendances/{id}`): 시스템 판정과 상관없이 관리자가 상태(예: 지각→출석)를 직접 수정.
    @PatchMapping("attendances/{id}")
    @Operation(summary = "출석 상태 변경(출석)", description = "시스템 판정과 상관없이 관리자가 상태(예: 지각→출석)를 직접 수정.")
    public ResponseEntity<ApiResponse<DailyAttendanceResponseDTO>> changeDailyAttendancePresentStatus(@PathVariable Long id) {
        DailyAttendanceResponseDTO result = adminStatsService.statusPresenceChange(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }


    //    - [ ]  **CSV/Excel 다운로드 API**: 현재 조회된 출석부 데이터를 파일로 변환하여 응답.
    @GetMapping("/download")
    @Operation(summary = "출석부 다운로드", description = "출석부를 다운 받을 수 있습니다.\n[필수 입력]\ntype(csv/excel)\n[선택 입력]\ndate & courseId (미입력시 전체 다운로드)")
    public ResponseEntity<ByteArrayResource> downloadAttendance(@RequestParam String downloadType,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
                                                                @RequestParam(required = false) Long courseId) {
        // 1. downloadType에 따른 파일 데이터 생성
        byte[] fileData = adminStatsService.downloadAttendanceStats(downloadType, workDate, courseId);

        // 2. 파일명 및 헤더 설정
        String fileName = "attendance_stats_" + LocalDate.now() + ("excel".equals(downloadType) ? ".xlsx" : ".csv");
        MediaType mediaType = "excel".equals(downloadType)
                ? MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                : MediaType.parseMediaType("text/csv");

        // 3. 파일 응답 반환
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(mediaType)
                .body(new ByteArrayResource(fileData));
    }
}
