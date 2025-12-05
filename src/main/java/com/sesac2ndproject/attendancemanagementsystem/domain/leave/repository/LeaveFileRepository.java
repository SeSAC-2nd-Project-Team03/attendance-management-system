package com.sesac2ndproject.attendancemanagementsystem.domain.leave.repository;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity.LeaveFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveFileRepository extends JpaRepository<LeaveFile, Long> {

    // Leave ID로 관련 파일 모두 조회
    List<LeaveFile> findByLeaveId(Long leaveId);

    // 파일명으로 조회
    Optional<LeaveFile> findByStoredFileName(String storedFileName);
}