package com.sesac2ndproject.attendancemanagementsystem.domain.leave.service;

import com.sesac2ndproject.attendancemanagementsystem.global.util.FileUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload.path:uploads/leave-files}")
    private String uploadPath;

    @Value("${file.upload.url:http://localhost:9090/api/v1/files}")
    private String uploadUrl;

    /**
     * 파일 로드 (리소스 반환) - 컨트롤러에서 이동된 로직
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = Paths.get(uploadPath).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("파일을 찾을 수 없습니다: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("파일 경로가 잘못되었습니다: " + fileName, e);
        }
    }

    public String getContentType(String fileName) {
        return FileUtil.getContentType(fileName);
    }

    /**
     * 파일 업로드 및 접근 가능한 URL 반환
     */
    public String upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        // 1. 파일 저장 디렉토리 생성
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 2. 고유한 파일명 생성
        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID() + "_" + originalFileName;
        Path filePath = uploadDir.resolve(uniqueFileName);

        // 3. 파일 저장
        Files.write(filePath, file.getBytes());

        // 4. 접근 가능한 URL 반환
        return uploadUrl + "/" + uniqueFileName;
    }



    /**
     * 파일 삭제
     */
    public void delete(String fileUrl) {
        try {
            // URL에서 파일명만 추출
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadPath).resolve(fileName);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            System.err.println("파일 삭제 실패: " + e.getMessage());
        }
    }

    /**
     * 파일 존재 여부 확인
     */
    public boolean fileExists(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadPath).resolve(fileName);
            return Files.exists(filePath);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 파일 크기 확인 (바이트 단위)
     */
    public long getFileSize(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadPath).resolve(fileName);
            return Files.size(filePath);
        } catch (IOException e) {
            return -1;
        }
    }
}