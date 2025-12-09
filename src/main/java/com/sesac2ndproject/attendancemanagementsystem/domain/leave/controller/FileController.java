package com.sesac2ndproject.attendancemanagementsystem.domain.leave.controller;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * [C] 파일 업로드 (생성)
     * POST /api/v1/files
     * Swagger에서 "Try it out"을 누르면 파일 선택 버튼이 나타납니다.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 서비스의 upload 메서드 호출 -> 저장된 파일의 URL 반환
            String fileUrl = fileService.upload(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 업로드 실패: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * [R] 파일 다운로드/조회 (읽기)
     * GET /api/v1/files/{fileName}
     */
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {

        Resource resource = fileService.loadFileAsResource(fileName);
        String contentType = fileService.getContentType(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }

    /**
     * [D] 파일 삭제 (삭제)
     * DELETE /api/v1/files/{fileName}
     */
    @DeleteMapping("/{fileName}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileName) {
        fileService.delete(fileName);
        return ResponseEntity.noContent().build();
    }
}