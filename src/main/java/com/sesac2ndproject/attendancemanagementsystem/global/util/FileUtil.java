package com.sesac2ndproject.attendancemanagementsystem.global.util;


public class FileUtil {

    // 인스턴스화 방지 (static 메서드만 쓸 거니까)
    private FileUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String getContentType(String fileName) {
        if (fileName == null) {
            return "application/octet-stream";
        }

        String lowerCaseName = fileName.toLowerCase();

        if (lowerCaseName.endsWith(".jpg") || lowerCaseName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerCaseName.endsWith(".png")) {
            return "image/png";
        } else if (lowerCaseName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerCaseName.endsWith(".pdf")) {
            return "application/pdf";
        }

        return "application/octet-stream";
    }
}
