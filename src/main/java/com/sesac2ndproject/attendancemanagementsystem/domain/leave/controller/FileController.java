package com.sesac2ndproject.attendancemanagementsystem.domain.leave.controller;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private final FileService fileService;

    /**
     * 파일 다운로드
     * GET /api/v1/files/{fileName}
     */
    @GetMapping("/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) {
        try {
            log.info("파일 다운로드 요청 - fileName: {}", fileName);

            // 파일 존재 여부 확인
            if (!fileService.fileExists(fileName)) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("파일을 찾을 수 없습니다");
            }

            // 파일 경로 생성
            Path filePath = Paths.get(uploadDir, fileName).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("파일을 찾을 수 없습니다");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .body(resource);

        } catch (MalformedURLException e) {
            log.error("파일 다운로드 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 다운로드 중 오류가 발생했습니다");
        }
    }
}