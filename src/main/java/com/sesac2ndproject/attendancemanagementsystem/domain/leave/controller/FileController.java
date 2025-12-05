package com.sesac2ndproject.attendancemanagementsystem.domain.leave.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

    @RestController
    @RequestMapping("/api/v1/files")
    public class FileController {

        @Value("${file.upload.path:uploads/leave-files}")
        private String uploadPath;

        /**
         * 파일 다운로드/조회
         * GET /api/v1/files/{fileName}
         */
        @GetMapping("/{fileName}")
        public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
            try {
                // 파일 경로 생성
                Path filePath = Paths.get(uploadPath).resolve(fileName).normalize();
                Resource resource = new UrlResource(filePath.toUri());

                // 파일 존재 확인
                if (!resource.exists()) {
                    return ResponseEntity.notFound().build();
                }

                // 파일 타입 결정
                String contentType = getContentType(fileName);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + fileName + "\"")
                        .body(resource);

            } catch (MalformedURLException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        /**
         * 파일 타입 판별
         */
        private String getContentType(String fileName) {
            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                return "image/jpeg";
            } else if (fileName.endsWith(".png")) {
                return "image/png";
            } else if (fileName.endsWith(".gif")) {
                return "image/gif";
            } else if (fileName.endsWith(".pdf")) {
                return "application/pdf";
            }
            return "application/octet-stream";
        }
    }
