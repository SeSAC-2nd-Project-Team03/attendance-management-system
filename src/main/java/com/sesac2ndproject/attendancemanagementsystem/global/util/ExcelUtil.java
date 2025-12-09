package com.sesac2ndproject.attendancemanagementsystem.global.util;

import com.sesac2ndproject.attendancemanagementsystem.domain.admin.dto.ResponseByDateAndCourseIdDTO;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.common.entity.DetailedAttendance;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelUtil {
    public static byte[] createExcelFile(List<ResponseByDateAndCourseIdDTO> dataList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baout = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("출석부");

            // 헤더 스타일 설정(밑의 헤더 생성에서 사용함)
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);   // 폰트(bold)
            headerStyle.setFont(headerFont);

            // 헤더 생성(Row 0)
            Row headerRow = sheet.createRow(0);
            String[] headers = {"날짜", "학생ID", "최종상태", "상세타입", "입력시간", "IP", "검증여부"};
            for(int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 데이터 생성(Row 1~n)
            int rowIdx = 1;
            for (ResponseByDateAndCourseIdDTO dto : dataList){
                Row row = sheet.createRow(rowIdx++); // 해당하는 rowIndex로 Row를 만든 후 +1

                row.createCell(0).setCellValue(dto.getDate().toString());
                row.createCell(1).setCellValue(dto.getMemberId());
                row.createCell(2).setCellValue(dto.getStatus().getDescription());
                // 상세 데이터(detailedAttendance) 존재 여부에 따라 넣을 값 결정
                if(dto.getDetailedAttendance() != null) {
                    DetailedAttendance detail = dto.getDetailedAttendance();
                    row.createCell(3).setCellValue(detail.getType().toString());
                    row.createCell(4).setCellValue(detail.getCheckTime().toString());
                    row.createCell(5).setCellValue(detail.getConnectionIp());
                    row.createCell(6).setCellValue(detail.isVerified() ? "성공" : "실패");
                } else {
                    // 상세 데이터가 없을 경우 빈칸
                    row.createCell(3).setCellValue("-");
                    row.createCell(4).setCellValue("-");
                    row.createCell(5).setCellValue("-");
                    row.createCell(6).setCellValue("-");
                }
            }

            workbook.write(baout);
            return baout.toByteArray();
        }
    }
}
