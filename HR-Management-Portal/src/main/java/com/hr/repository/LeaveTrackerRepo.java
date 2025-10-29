package com.hr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hr.entity.LeaveTracker;

@Repository
public interface LeaveTrackerRepo extends JpaRepository<LeaveTracker, Integer> {
    
    List<LeaveTracker> findByEmployeeId(Integer employeeId);
    
    Optional<LeaveTracker> findFirstByEmployeeId(Integer employeeId);
}
