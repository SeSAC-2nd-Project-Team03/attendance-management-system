package com.sesac2ndproject.attendancemanagementsystem.domain.admin.service;

import com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto.*;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.repository.DailyAttendanceRepository;
import com.sesac2ndproject.attendancemanagementsystem.domain.course.entity.Enrollment;
import com.sesac2ndproject.attendancemanagementsystem.domain.course.repository.EnrollmentRepository;
import com.sesac2ndproject.attendancemanagementsystem.global.type.EnrollmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회 성능 최적화
public class AdminStatsService {

    private final EnrollmentRepository enrollmentRepository;
    private final DailyAttendanceRepository dailyAttendanceRepository;
    // - **조회 로직 (Query)**
    //    - [ ]  **과정별 수강생 조회:** `Enrollment`를 통해 특정 과정(Course)을 듣는 `memberId` 목록 추출.
    public List<Enrollment> findMemberIdsByCourseId(Long courseId) {
        List<Enrollment> foundEnrollment = enrollmentRepository.findMemberIdByCourseId(courseId);
        return foundEnrollment;
    }
    //    - [ ]  **통합 출석부 조회:** 날짜 + 과정ID를 받으면 → 해당 수강생들의 `DailyAttendance`와 `DetailedAttendance`를 조인(또는 Fetch)하여 가져오기.

    public List<ResponseByDateAndCourseIdDTO> findByDateAndCourseId(LocalDate workDate, Long courseId) {
        List<ResponseByDateAndCourseIdDTO> foundList = enrollmentRepository.integratedAttendance(workDate,courseId);
        return foundList;
    }

    //- **API 개발 (관리자용)**
    //    - [ ]  **전체 출석 현황 조회 API** (`GET /api/v1/admin/attendances`): 날짜별, 과정별 전체 학생의 출석 상태 리스트 반환14.
    // 쿼리: "특정 날짜(date)에, 이 학생들(memberIds)의 출석부 다 가져와"
    // (Team D가 Enrollment에서 학생 ID 목록을 먼저 구해오면, 여기서 그 ID들로 조회함)
    // 날짜 -> DAILY_ATTENDANCE 반환. COURSE의 start_date와 end_date 사이에 있는 코스를 반환.
    public List<ResponseAttendanceByDateDTO> getDailyAttendance(LocalDate workDate, Long courseId) {

        // 1. DB에서 납작한 데이터 가져오기 (FlatResponse 리스트)
        List<ResponseAttendanceByDateDTO.FlatResponse> flatList =
                enrollmentRepository.findIntegratedAttendanceFlat(workDate, courseId);

        // 2. 조립을 위한 Map 생성 (Key: 일일출석부 ID)
        Map<Long, ResponseAttendanceByDateDTO> resultMap = new HashMap<>();

        for (ResponseAttendanceByDateDTO.FlatResponse flat : flatList) {
            Long id = flat.getDailyAttendanceId();

            // 2-1. Map에 해당 출석부(Key)가 없으면 바구니(DTO) 새로 만들기
            if (!resultMap.containsKey(id)) {
                ResponseAttendanceByDateDTO dto = ResponseAttendanceByDateDTO.builder()
                        .dailyAttendanceId(flat.getDailyAttendanceId())
                        .memberId(flat.getMemberId())
                        .courseId(flat.getCourseId())
                        .workDate(flat.getWorkDate())
                        .totalStatus(flat.getTotalStatus())
                        .detailedAttendanceList(new ArrayList<>()) // 리스트 초기화
                        .build();
                resultMap.put(id, dto);
            }

            // 2-2. 상세 출석 기록(detail)이 존재하면 리스트에 추가
            // (LEFT JOIN이라 null일 수도 있으므로 체크)
            if (flat.getDetailedAttendance() != null) {
                resultMap.get(id).addDetail(flat.getDetailedAttendance());
            }
        }

        // 3. Map의 값들(Value)만 리스트로 변환하여 반환
        return new ArrayList<>(resultMap.values());
    }

    //    - [ ]  **조퇴/결석 승인 처리 API** (`PATCH /api/v1/admin/leaves/{id}`): 신청 상태를 `APPROVED`로 변경15.
    //        - *(Tip: 승인 시 Team B의 `DailyAttendance` 상태를 업데이트하는 로직을 호출하거나, Team B와 협의 필요)*
    //    - [ ]  **출석 상태 강제 변경 API** (`PUT /api/v1/admin/attendances/{id}`): 시스템 판정과 상관없이 관리자가 상태(예: 지각→출석)를 직접 수정16.
    //    - [ ]  **CSV/Excel 다운로드 API**: 현재 조회된 출석부 데이터를 파일로 변환하여 응답17.
}
