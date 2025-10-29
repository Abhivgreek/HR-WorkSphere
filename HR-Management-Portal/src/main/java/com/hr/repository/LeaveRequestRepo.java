package com.hr.repository;

import com.hr.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeaveRequestRepo extends JpaRepository<LeaveRequest, Integer> {
    
    List<LeaveRequest> findByEmployeeId(Integer employeeId);
    
    List<LeaveRequest> findByStatus(String status);
    
    List<LeaveRequest> findByEmployeeIdAndStatus(Integer employeeId, String status);
    
    List<LeaveRequest> findByLeaveType(String leaveType);
}
