package com.sesac2ndproject.attendancemanagementsystem.domain.notice.repository;

import com.sesac2ndproject.attendancemanagementsystem.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 3. 팝업 설정이 True 이고 현재 날짜가 시작일과 종료일 사이인 것만 조회
    @Query("SELECT n FROM Notice n WHERE n.isPopup = true AND n.popupStartDate <= :now AND n.popupEndDate >= :now")
    List<Notice> findActivePopups(@Param("now")LocalDateTime now);
}
