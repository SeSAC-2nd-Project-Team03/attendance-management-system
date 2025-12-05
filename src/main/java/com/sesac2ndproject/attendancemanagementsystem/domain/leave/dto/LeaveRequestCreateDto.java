package com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity.LeaveType;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequestCreateDto {
    private LocalDate leaveDate;      // 휴가 날짜 추가
    private LeaveType leaveType;      // 휴가 타입
    private String reason;            // 신청 사유
    private MultipartFile file;       // 증빙 서류
}