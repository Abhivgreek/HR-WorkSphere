package com.hr.service;

import com.hr.dto.LeaveTrackerDTO;
import com.hr.dto.LeaveRequestDTO;
import com.hr.entity.Employee;
import com.hr.entity.LeaveTracker;
import com.hr.entity.LeaveRequest;
import com.hr.repository.EmployeeRepo;
import com.hr.repository.LeaveTrackerRepo;
import com.hr.repository.LeaveRequestRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LeaveService {

    private final LeaveTrackerRepo leaveTrackerRepo;
    private final LeaveRequestRepo leaveRequestRepo;
    private final EmployeeRepo employeeRepo;

    public List<LeaveTrackerDTO> getAllLeaves() {
        List<LeaveTracker> leaves = leaveTrackerRepo.findAll();
        return leaves.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Optional<LeaveTrackerDTO> getLeaveById(Integer id) {
        return leaveTrackerRepo.findById(id)
                .map(this::convertToDTO);
    }

    public List<LeaveTrackerDTO> getLeavesByEmployee(Integer employeeId) {
        List<LeaveTracker> leaves = leaveTrackerRepo.findByEmployeeId(employeeId);
        return leaves.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public LeaveTrackerDTO createLeave(LeaveTrackerDTO leaveDTO) {
        LeaveTracker leave = convertToEntity(leaveDTO);
        LeaveTracker saved = leaveTrackerRepo.save(leave);
        log.info("Created leave request with ID: {}", saved.getId());
        return convertToDTO(saved);
    }

    public Optional<LeaveTrackerDTO> updateLeave(Integer id, LeaveTrackerDTO leaveDTO) {
        return leaveTrackerRepo.findById(id)
                .map(existing -> {
                    updateEntityFromDTO(existing, leaveDTO);
                    LeaveTracker updated = leaveTrackerRepo.save(existing);
                    log.info("Updated leave request with ID: {}", updated.getId());
                    return convertToDTO(updated);
                });
    }

    public boolean deleteLeave(Integer id) {
        return leaveTrackerRepo.findById(id)
                .map(leave -> {
                    leaveTrackerRepo.delete(leave);
                    log.info("Deleted leave request with ID: {}", id);
                    return true;
                })
                .orElse(false);
    }

    public List<LeaveTrackerDTO> getLeavesByStatus(String status) {
        List<LeaveTracker> leaves = leaveTrackerRepo.findAll().stream()
                .filter(l -> status == null || l.getStatus().equalsIgnoreCase(status))
                .toList();
        
        return leaves.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<LeaveTrackerDTO> getLeavesByDateRange(String fromDate, String toDate) {
        List<LeaveTracker> leaves = leaveTrackerRepo.findAll().stream()
                .filter(l -> isWithinDateRange(l.getFromDate(), l.getToDate(), fromDate, toDate))
                .toList();
        
        return leaves.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private boolean isWithinDateRange(String leaveFromDate, String leaveToDate, String fromDate, String toDate) {
        try {
            LocalDate leaveStart = LocalDate.parse(leaveFromDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate leaveEnd = LocalDate.parse(leaveToDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate rangeStart = fromDate != null ? LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
            LocalDate rangeEnd = toDate != null ? LocalDate.parse(toDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
            
            if (rangeStart != null && leaveEnd.isBefore(rangeStart)) return false;
            if (rangeEnd != null && leaveStart.isAfter(rangeEnd)) return false;
            return true;
        } catch (Exception e) {
            log.warn("Error parsing dates for leave date range filter: {}", e.getMessage());
            return true;
        }
    }

    // Conversion methods
    private LeaveTrackerDTO convertToDTO(LeaveTracker leave) {
        LeaveTrackerDTO dto = LeaveTrackerDTO.builder()
                .id(leave.getId())
                .employeeId(leave.getEmployeeId())
                .fromDate(leave.getFromDate())
                .toDate(leave.getToDate())
                .numberOfDays(leave.getNumberOfDays())
                .leaveType(leave.getLeaveType())
                .reason(leave.getReason())
                .status(leave.getStatus())
                .appliedDate(leave.getAppliedDate())
                .approverComments(leave.getApproverComments())
                .createdDate(leave.getCreatedDate())
                .updatedDate(leave.getUpdatedDate())
                .build();

        // Calculate computed fields and add employee info
        calculateComputedFields(dto);
        return dto;
    }

    private LeaveTracker convertToEntity(LeaveTrackerDTO dto) {
        LeaveTracker leave = new LeaveTracker();
        leave.setEmployeeId(dto.getEmployeeId());
        leave.setFromDate(dto.getFromDate());
        leave.setToDate(dto.getToDate());
        leave.setLeaveType(dto.getLeaveType());
        leave.setReason(dto.getReason());
        leave.setStatus(dto.getStatus() != null ? dto.getStatus() : "PENDING");
        leave.setAppliedDate(dto.getAppliedDate() != null ? dto.getAppliedDate() : 
                            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        leave.setApproverComments(dto.getApproverComments());
        
        // Calculate number of days
        calculateNumberOfDays(leave);
        return leave;
    }

    private void updateEntityFromDTO(LeaveTracker leave, LeaveTrackerDTO dto) {
        if (dto.getEmployeeId() != null) leave.setEmployeeId(dto.getEmployeeId());
        if (dto.getFromDate() != null) leave.setFromDate(dto.getFromDate());
        if (dto.getToDate() != null) leave.setToDate(dto.getToDate());
        if (dto.getLeaveType() != null) leave.setLeaveType(dto.getLeaveType());
        if (dto.getReason() != null) leave.setReason(dto.getReason());
        if (dto.getStatus() != null) leave.setStatus(dto.getStatus());
        if (dto.getApproverComments() != null) leave.setApproverComments(dto.getApproverComments());
        
        // Recalculate number of days if dates changed
        calculateNumberOfDays(leave);
    }

    private void calculateNumberOfDays(LeaveTracker leave) {
        if (leave.getFromDate() != null && leave.getToDate() != null) {
            try {
                LocalDate fromDate = LocalDate.parse(leave.getFromDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate toDate = LocalDate.parse(leave.getToDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                long days = ChronoUnit.DAYS.between(fromDate, toDate) + 1; // +1 to include both start and end date
                leave.setNumberOfDays((int) days);
            } catch (Exception e) {
                log.warn("Error calculating number of days for leave: {}", e.getMessage());
                leave.setNumberOfDays(1);
            }
        }
    }

    private void calculateComputedFields(LeaveTrackerDTO dto) {
        // Get employee information
        if (dto.getEmployeeId() != null) {
            Optional<Employee> employee = employeeRepo.findById(dto.getEmployeeId());
            employee.ifPresent(emp -> {
                dto.setEmployeeName(emp.getEmployeeName());
                dto.setDepartment(emp.getDepartment());
                dto.setDesignation(emp.getDesignation());
            });
        }
    }

    private String getStatusDisplay(String status) {
        return switch (status.toUpperCase()) {
            case "PENDING" -> "Pending";
            case "APPROVED" -> "Approved";
            case "REJECTED" -> "Rejected";
            case "CANCELLED" -> "Cancelled";
            default -> status;
        };
    }

    private String getLeaveTypeDisplay(String leaveType) {
        return switch (leaveType.toUpperCase()) {
            case "SICK" -> "Sick Leave";
            case "CASUAL" -> "Casual Leave";
            case "VACATION" -> "Vacation";
            case "PERSONAL" -> "Personal Leave";
            case "MATERNITY" -> "Maternity Leave";
            case "PATERNITY" -> "Paternity Leave";
            case "EMERGENCY" -> "Emergency Leave";
            default -> leaveType;
        };
    }

    // Method to convert DTO to Map for controller compatibility
    public Map<String, Object> convertDtoToMap(LeaveTrackerDTO dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", dto.getId());
        map.put("employeeId", dto.getEmployeeId());
        map.put("employeeName", dto.getEmployeeName());
        map.put("department", dto.getDepartment());
        map.put("designation", dto.getDesignation());
        map.put("fromDate", dto.getFromDate());
        map.put("toDate", dto.getToDate());
        map.put("numberOfDays", dto.getNumberOfDays());
        map.put("leaveType", dto.getLeaveType());
        map.put("reason", dto.getReason());
        map.put("status", dto.getStatus());
        map.put("appliedDate", dto.getAppliedDate());
        map.put("approverComments", dto.getApproverComments());
        map.put("createdDate", dto.getCreatedDate());
        map.put("updatedDate", dto.getUpdatedDate());
        return map;
    }
    
    // Methods for leave balance management
    public LeaveTrackerDTO getLeaveBalance(int employeeId) {
        Optional<LeaveTracker> tracker = leaveTrackerRepo.findFirstByEmployeeId(employeeId);
        if (tracker.isPresent()) {
            return convertToDTO(tracker.get());
        } else {
            // Create a new leave tracker if one doesn't exist
            LeaveTracker newTracker = new LeaveTracker();
            newTracker.setEmployeeId(employeeId);
            newTracker.setTotalLeaves(40);
            newTracker.setUsedLeaves(0);
            LeaveTracker saved = leaveTrackerRepo.save(newTracker);
            return convertToDTO(saved);
        }
    }
    
    public void deductLeaves(Integer employeeId, Integer leaveDays) {
        Optional<LeaveTracker> tracker = leaveTrackerRepo.findFirstByEmployeeId(employeeId);
        if (tracker.isPresent()) {
            LeaveTracker leaveTracker = tracker.get();
            int currentUsed = leaveTracker.getUsedLeaves() != null ? leaveTracker.getUsedLeaves() : 0;
            leaveTracker.setUsedLeaves(currentUsed + leaveDays);
            leaveTrackerRepo.save(leaveTracker);
            log.info("Deducted {} leave days from employee {}", leaveDays, employeeId);
        } else {
            throw new RuntimeException("Leave tracker not found for employee: " + employeeId);
        }
    }
    
    public boolean hasEnoughLeaves(int employeeId, Integer requestedDays) {
        try {
            LeaveTrackerDTO balance = getLeaveBalance(employeeId);
            return balance.getAvailableLeaves() >= requestedDays;
        } catch (Exception e) {
            log.warn("Error checking leave balance for employee {}: {}", employeeId, e.getMessage());
            return false;
        }
    }
}
