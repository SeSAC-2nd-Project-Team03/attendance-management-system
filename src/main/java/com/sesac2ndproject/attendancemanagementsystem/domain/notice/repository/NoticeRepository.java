package com.sesac2ndproject.attendancemanagementsystem.domain.notice.repository;

import com.sesac2ndproject.attendancemanagementsystem.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

}
