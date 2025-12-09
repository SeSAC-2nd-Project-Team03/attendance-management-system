package com.sesac2ndproject.attendancemanagementsystem.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class NoticeResponseDTO {

    // 목록 조회용(본문을 제외하여 가벼움)
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ListDTO {
        private Long id;
        private String title;
        private String writerName;
        private Long viewCount;
        private Boolean isPopup;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    // 상세 조회 및 팝업용(본문 포함)
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailDTO {
        private Long id;
        private String title;
        private String content; // 본문 포함
        private String writerName;
        private Long viewCount;
        private Boolean isPopup;
        private LocalDateTime popupStartDate;
        private LocalDateTime popupEndDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
