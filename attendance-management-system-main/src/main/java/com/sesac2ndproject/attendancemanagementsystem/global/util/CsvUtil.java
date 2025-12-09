package com.sesac2ndproject.attendancemanagementsystem.global.util;

import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.query.dto.ResponseAttendanceFlatDTO;
import com.sesac2ndproject.attendancemanagementsystem.domain.attendance.common.entity.DetailedAttendance;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvUtil {

    // 1. CSV 파일 생성 (콤마로 구분)
    public static byte[] createCsvFile(List<ResponseAttendanceFlatDTO> dataList) {
        StringBuilder sb = new StringBuilder();

        // Header 작성
        sb.append("날짜,학생ID,학생이름,최종상태,강좌ID,강좌이름,출석타입,입력시간,IP,검증여부\n");

        // Row(데이터) 작성
        for(ResponseAttendanceFlatDTO dto : dataList) {
            sb.append(dto.getWorkDate()).append(",");
            sb.append(dto.getMemberId()).append(",");
            sb.append(dto.getMemberName()).append(",");
            sb.append(dto.getTotalStatus()).append(",");
            sb.append(dto.getCourseId()).append(",");
            sb.append(dto.getCourseName()).append(",");
            // 상세 기록이 없는 경우(결석 등) 빈칸 처리
            if(dto.getDetailedAttendance() != null) {
                DetailedAttendance detail = dto.getDetailedAttendance();
                sb.append(detail.getType()).append(",");
                sb.append(detail.getCheckTime()).append(",");
                sb.append(detail.getConnectionIp()).append(",");
                sb.append(detail.isVerified() ? "O" : "X"); // 관리자의 승인이 있는 경우 O , 없으면 X
            } else {
                sb.append(",,,"); // 상세 기록이 없는 경우 3칸을 비운다.
            }
            sb.append("\n"); // 줄 바꾸기
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);  // 표준 형식인 UTF-8로 반환. 만약 한글 깨질 경우 UTF_8 BOM 같은 형식으로 바꿀 것.
    }
}
