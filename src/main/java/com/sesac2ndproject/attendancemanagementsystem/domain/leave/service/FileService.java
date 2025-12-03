package com.sesac2ndproject.attendancemanagementsystem.domain.leave.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileService {

    /**
     * 파일을 저장하고 접근 가능한 URL 반환
     */
    Map<String, Object> saveFile(MultipartFile file);

    /**
     * 저장된 파일 삭제
     */
    void deleteFile(String storedFileName);

    /**
     * 파일 존재 여부 확인
     */
    boolean fileExists(String storedFileName);
}