package com.sesac2ndproject.attendancemanagementsystem.domain.leave.repository;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity.LeaveFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveFileRepository extends JpaRepository<LeaveFile, Long> {

    // 변경된 필드명(leaveRequest)에 맞춰 메서드명 수정: findByLeaveId -> findByLeaveRequestId
    List<LeaveFile> findByLeaveRequest_Id(Long leaveRequestId);

    // 필요시 유지, 사용 안 하면 삭제 가능
    void deleteByLeaveRequest_Id(Long leaveRequestId);
}