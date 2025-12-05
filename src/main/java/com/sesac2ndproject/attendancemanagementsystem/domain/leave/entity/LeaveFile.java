package com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "leave_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_id", nullable = false)
    private Leave leave;

    // 원본 파일명
    @Column(nullable = false)
    private String originalFileName;

    // 저장된 파일명
    @Column(nullable = false)
    private String storedFileName;

    // 파일 경로 (접근 가능한 URL)
    @Column(nullable = false)
    private String filePath;

    // 파일 크기 (바이트)
    @Column
    private Long fileSize;

    // 파일 MIME 타입
    @Column
    private String mimeType;

    // 생성 일시
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Builder 메서드 직접 구현
    public static LeaveFileBuilder builder() {
        return new LeaveFileBuilder();
    }

    public static class LeaveFileBuilder {
        private Long id;
        private Leave leave;
        private String originalFileName;
        private String storedFileName;
        private String filePath;
        private Long fileSize;
        private String mimeType;
        private LocalDateTime createdAt;

        public LeaveFileBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public LeaveFileBuilder leave(Leave leave) {
            this.leave = leave;
            return this;
        }

        public LeaveFileBuilder originalFileName(String originalFileName) {
            this.originalFileName = originalFileName;
            return this;
        }

        public LeaveFileBuilder storedFileName(String storedFileName) {
            this.storedFileName = storedFileName;
            return this;
        }

        public LeaveFileBuilder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public LeaveFileBuilder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public LeaveFileBuilder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public LeaveFileBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public LeaveFile build() {
            LeaveFile leaveFile = new LeaveFile();
            leaveFile.id = this.id;
            leaveFile.leave = this.leave;
            leaveFile.originalFileName = this.originalFileName;
            leaveFile.storedFileName = this.storedFileName;
            leaveFile.filePath = this.filePath;
            leaveFile.fileSize = this.fileSize;
            leaveFile.mimeType = this.mimeType;
            leaveFile.createdAt = this.createdAt;
            return leaveFile;
        }
    }
}