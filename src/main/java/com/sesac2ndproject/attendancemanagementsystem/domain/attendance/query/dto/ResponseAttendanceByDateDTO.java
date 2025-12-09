package com.sesac2ndproject.attendancemanagementsystem.domain.attendance.query.dto;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.common.entity.DetailedAttendance;
import com.sesac2ndproject.attendancemanagementsystem.global.type.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//@Getter
//@AllArgsConstructor
//public class ResponseAttendanceByDateDTO {
//    private Long id;
//    private Long memberId;
//    private LocalDate date;
//    private AttendanceStatus attendanceStatus;
//    private Long memberName;
//    private String courseName;
//    private EnrollmentStatus enrollmentStatus;
//}
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAttendanceByDateDTO {

    // [최종 결과] 화면에 보여줄 완성된 형태
    private Long dailyAttendanceId;
    private Long memberId;
    private String memberName;
    private Long courseId;
    private String courseName;
    private LocalDate workDate;
    private AttendanceStatus totalStatus;

    // 상세 기록들을 리스트로 담음
    @Builder.Default
    private List<DetailedAttendance> detailedAttendanceList = new ArrayList<>();

    // 리스트에 하나씩 추가하는 편의 메서드
    public void addDetail(DetailedAttendance detail) {
        if (detail != null) {
            this.detailedAttendanceList.add(detail);
        }
    }

}
