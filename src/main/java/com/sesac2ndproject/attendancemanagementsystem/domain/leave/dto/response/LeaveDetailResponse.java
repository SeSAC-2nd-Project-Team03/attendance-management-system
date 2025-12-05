package com.sesac2ndproject.attendancemanagementsystem.domain.leave.dto.response;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity.Leave;
import com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity.LeaveFile;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveDetailResponse {

    private Long id;
    private String leaveType;
    private LocalDate leaveDate;
    private String reason;
    private String status;
    private Long approvedBy;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FileInfo> files;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FileInfo {
        private Long fileId;
        private String originalFileName;
        private String filePath;
        private Long fileSize;
        private String mimeType;
        private LocalDateTime createdAt;

        public static FileInfo from(LeaveFile leaveFile) {
            return FileInfo.builder()
                    .fileId(leaveFile.getId())
                    .originalFileName(leaveFile.getOriginalFileName())
                    .filePath(leaveFile.getFilePath())
                    .fileSize(leaveFile.getFileSize())
                    .mimeType(leaveFile.getMimeType())
                    .createdAt(leaveFile.getCreatedAt())
                    .build();
        }
    }

    public static LeaveDetailResponse from(Leave leave, List<LeaveFile> files) {
        return LeaveDetailResponse.builder()
                .id(leave.getId())
                .leaveType(leave.getLeaveType().name())
                .leaveDate(leave.getLeaveDate())
                .reason(leave.getReason())
                .status(leave.getStatus().name())
                .approvedBy(leave.getApprovedBy())
                .rejectionReason(leave.getRejectionReason())
                .createdAt(leave.getCreatedAt())
                .updatedAt(leave.getUpdatedAt())
                .files(files.stream()
                        .map(FileInfo::from)
                        .collect(Collectors.toList()))
                .build();
    }
}