package com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveCreateRequest {

    @NotBlank(message = "휴가 유형은 필수입니다")
    private String leaveType;  // SICK_LEAVE, VACATION_LEAVE, PERSONAL_LEAVE, EARLY_LEAVE

    @NotNull(message = "신청 날짜는 필수입니다")
    private LocalDate leaveDate;

    @NotBlank(message = "신청 사유는 필수입니다")
    private String reason;

    // 첨부 파일
    private List<MultipartFile> files;
}