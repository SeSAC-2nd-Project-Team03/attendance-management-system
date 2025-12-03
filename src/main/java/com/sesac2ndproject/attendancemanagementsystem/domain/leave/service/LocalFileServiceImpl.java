package com.sesac2ndproject.attendancemanagementsystem.domain.leave.service;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
public class LocalFileServiceImpl implements FileService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080}")
    private String baseUrl;

    // 허용된 파일 확장자
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(
            Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "jpg", "jpeg", "png", "gif", "txt")
    );

    // 최대 파일 크기 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Override
    public Map<String, Object> saveFile(MultipartFile file) {
        try {
            // 파일 검증
            validateFile(file);

            // 디렉토리 생성
            createUploadDirectory();

            // 파일명 생성 (중복 방지)
            String storedFileName = generateUniqueFileName(file.getOriginalFilename());

            // 파일 저장 경로
            Path uploadPath = Paths.get(uploadDir, storedFileName);

            // 파일 저장
            file.transferTo(uploadPath);

            // 접근 가능한 URL 생성
            String filePath = String.format("%s/api/v1/files/%s", baseUrl, storedFileName);

            log.info("파일 저장 완료: {} -> {}", file.getOriginalFilename(), storedFileName);

            // 반환 정보
            Map<String, Object> result = new HashMap<>();
            result.put("storedFileName", storedFileName);
            result.put("filePath", filePath);
            result.put("fileSize", file.getSize());
            result.put("mimeType", file.getContentType());

            return result;

        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생", e);
            throw new FileException("파일 저장에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String storedFileName) {
        try {
            Path filePath = Paths.get(uploadDir, storedFileName);
            Files.deleteIfExists(filePath);
            log.info("파일 삭제 완료: {}", storedFileName);
        } catch (IOException e) {
            log.error("파일 삭제 중 오류 발생: {}", storedFileName, e);
            throw new FileException("파일 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public boolean fileExists(String storedFileName) {
        Path filePath = Paths.get(uploadDir, storedFileName);
        return Files.exists(filePath);
    }

    /**
     * 파일 검증
     */
    private void validateFile(MultipartFile file) {
        // 파일이 비어있지 않은지 확인
        if (file.isEmpty()) {
            throw new FileException("업로드할 파일이 없습니다");
        }

        // 파일 크기 확인
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileException("파일 크기가 10MB를 초과합니다");
        }

        // 파일 확장자 확인
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new FileException("허용되지 않는 파일 형식입니다. 허용 형식: " + ALLOWED_EXTENSIONS);
        }
    }

    /**
     * 업로드 디렉토리 생성
     */
    private void createUploadDirectory() {
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("업로드 디렉토리 생성: {}", uploadDir);
            }
        } catch (IOException e) {
            log.error("디렉토리 생성 중 오류 발생", e);
            throw new FileException("업로드 디렉토리를 생성할 수 없습니다");
        }
    }

    /**
     * 중복되지 않는 파일명 생성
     */
    private String generateUniqueFileName(String originalFileName) {
        String baseName = FilenameUtils.getBaseName(originalFileName);
        String extension = FilenameUtils.getExtension(originalFileName);
        String timestamp = System.currentTimeMillis() + "";
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        return String.format("%s_%s_%s.%s", baseName, timestamp, uuid, extension);
    }

}