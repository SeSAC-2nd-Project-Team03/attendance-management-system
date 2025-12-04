package com.sesac2ndproject.attendancemanagementsystem.domain.leave.repository;

import com.sesac2ndproject.attendancemanagementsystem.domain.leave.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

}
