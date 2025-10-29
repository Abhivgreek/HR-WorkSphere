package com.hr.dto;

import com.hr.entity.Employee;
import com.hr.entity.LeaveTracker;
import com.hr.entity.Payroll;
import com.hr.entity.CreatePost;
import com.hr.entity.Compose;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for mapping between Entity and DTO objects
 * Provides clean separation of concerns and data transformation
 */
@Component
public class DtoMapper {

    // Employee mapping methods
    public EmployeeDTO toEmployeeDTO(Employee employee) {
        if (employee == null) return null;

        return EmployeeDTO.builder()
                .id(employee.getId())
                .employeeName(employee.getEmployeeName())
                .email(employee.getEmail())
                .gender(employee.getGender())
                .dateOfBirth(employee.getDateOfBirth())
                .joinDate(employee.getJoinDate())
                .mobileNumber(employee.getMobileNumber())
                .aadhaarNumber(employee.getAadhaarNumber())
                .accountNumber(employee.getAccountNumber())
                .department(employee.getDepartment())
                .designation(employee.getDesignation())
                .previousCompany(employee.getPreviousCompany())
                .pfNumber(employee.getPfNumber())
                .salary(employee.getSalary())
                .permanentAddress(employee.getPermanrntAddress()) // Note: typo in entity
                .active(employee.isActive())
                .createdDate(employee.getCreatedDate())
                .updatedDate(employee.getUpdatedDate())
                .role(employee.getRole())
                // Map currentAddress to address field (primary address field)
                .address(employee.getCurrentAddress() != null ? employee.getCurrentAddress() : employee.getAddress())
                .build();
    }

    public Employee toEmployee(EmployeeDTO dto) {
        if (dto == null) return null;

        Employee employee = new Employee();
        employee.setId(dto.getId());
        employee.setEmployeeName(dto.getEmployeeName());
        employee.setEmail(dto.getEmail());
        employee.setGender(dto.getGender());
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setJoinDate(dto.getJoinDate());
        employee.setMobileNumber(dto.getMobileNumber());
        employee.setAadhaarNumber(dto.getAadhaarNumber());
        employee.setAccountNumber(dto.getAccountNumber());
        employee.setDepartment(dto.getDepartment());
        employee.setDesignation(dto.getDesignation());
        employee.setPreviousCompany(dto.getPreviousCompany());
        employee.setPfNumber(dto.getPfNumber());
        employee.setSalary(dto.getSalary());
        employee.setPermanrntAddress(dto.getPermanentAddress()); // Note: typo in entity
        employee.setActive(dto.getActive() != null ? dto.getActive() : true);
        employee.setRole(dto.getRole());
        // Map address to currentAddress field (primary address field)
        employee.setCurrentAddress(dto.getAddress());
        employee.setAddress(dto.getAddress()); // Keep both for compatibility

        return employee;
    }

    public List<EmployeeDTO> toEmployeeDTOList(List<Employee> employees) {
        if (employees == null) return null;
        return employees.stream()
                .map(this::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    // Payroll mapping methods
    public PayrollDTO toPayrollDTO(Payroll payroll) {
        if (payroll == null) return null;

        PayrollDTO dto = new PayrollDTO();
        dto.setId(payroll.getId());
        dto.setEmployeeId(payroll.getEmployeeId());
        dto.setBasicSalary(payroll.getBasicSalary());
        dto.setHra(payroll.getHra());
        dto.setTransportAllowance(payroll.getTransportAllowance());
        dto.setMedicalAllowance(payroll.getMedicalAllowance());
        dto.setOtherAllowances(payroll.getOtherAllowances());
        dto.setPfDeduction(payroll.getPfDeduction());
        dto.setEsiDeduction(payroll.getEsiDeduction());
        dto.setProfessionalTax(payroll.getProfessionalTax());
        dto.setIncomeTax(payroll.getIncomeTax());
        dto.setInsuranceDeduction(payroll.getInsuranceDeduction());
        dto.setOtherDeductions(payroll.getOtherDeductions());
        dto.setGrossSalary(payroll.getGrossSalary());
        dto.setTotalDeductions(payroll.getTotalDeductions());
        dto.setNetSalary(payroll.getNetSalary());
        dto.setPayrollMonth(payroll.getPayrollMonth());
        dto.setPayrollYear(payroll.getPayrollYear());
        dto.setWorkingDays(payroll.getWorkingDays());
        dto.setPresentDays(payroll.getPresentDays());
        dto.setLeaveDays(payroll.getLeaveDays());
        dto.setStatus(payroll.getStatus());
        dto.setCreatedDate(payroll.getCreatedDate());
        dto.setUpdatedDate(payroll.getUpdatedDate());

        return dto;
    }

    public PayrollDTO toPayrollDTOWithEmployee(Payroll payroll, Employee employee) {
        PayrollDTO dto = toPayrollDTO(payroll);
        if (dto != null && employee != null) {
            dto.setEmployeeName(employee.getEmployeeName());
            dto.setEmployeeEmail(employee.getEmail());
            dto.setDepartment(employee.getDepartment());
            dto.setDesignation(employee.getDesignation());
        }
        return dto;
    }

    public Payroll toPayroll(PayrollDTO dto) {
        if (dto == null) return null;

        Payroll payroll = new Payroll();
        payroll.setId(dto.getId());
        payroll.setEmployeeId(dto.getEmployeeId());
        payroll.setBasicSalary(dto.getBasicSalary());
        payroll.setHra(dto.getHra());
        payroll.setTransportAllowance(dto.getTransportAllowance());
        payroll.setMedicalAllowance(dto.getMedicalAllowance());
        payroll.setOtherAllowances(dto.getOtherAllowances());
        payroll.setPfDeduction(dto.getPfDeduction());
        payroll.setEsiDeduction(dto.getEsiDeduction());
        payroll.setProfessionalTax(dto.getProfessionalTax());
        payroll.setIncomeTax(dto.getIncomeTax());
        payroll.setInsuranceDeduction(dto.getInsuranceDeduction());
        payroll.setOtherDeductions(dto.getOtherDeductions());
        payroll.setGrossSalary(dto.getGrossSalary());
        payroll.setTotalDeductions(dto.getTotalDeductions());
        payroll.setNetSalary(dto.getNetSalary());
        payroll.setPayrollMonth(dto.getPayrollMonth());
        payroll.setPayrollYear(dto.getPayrollYear());
        payroll.setWorkingDays(dto.getWorkingDays());
        payroll.setPresentDays(dto.getPresentDays());
        payroll.setLeaveDays(dto.getLeaveDays());
        payroll.setStatus(dto.getStatus());

        return payroll;
    }

    public List<PayrollDTO> toPayrollDTOList(List<Payroll> payrolls) {
        if (payrolls == null) return null;
        return payrolls.stream()
                .map(this::toPayrollDTO)
                .collect(Collectors.toList());
    }

    // LeaveTracker mapping methods
    public LeaveTrackerDTO toLeaveTrackerDTO(LeaveTracker leaveTracker) {
        if (leaveTracker == null) return null;

        LeaveTrackerDTO dto = new LeaveTrackerDTO();
        dto.setId(leaveTracker.getId());
        dto.setEmployeeId(leaveTracker.getEmployeeId());
        dto.setTotalLeaves(leaveTracker.getTotalLeaves());
        dto.setUsedLeaves(leaveTracker.getUsedLeaves());

        return dto;
    }

    public LeaveTrackerDTO toLeaveTrackerDTOWithEmployee(LeaveTracker leaveTracker, Employee employee) {
        LeaveTrackerDTO dto = toLeaveTrackerDTO(leaveTracker);
        if (dto != null && employee != null) {
            dto.setEmployeeName(employee.getEmployeeName());
            dto.setEmployeeEmail(employee.getEmail());
            dto.setDepartment(employee.getDepartment());
            dto.setDesignation(employee.getDesignation());
        }
        return dto;
    }

    public LeaveTracker toLeaveTracker(LeaveTrackerDTO dto) {
        if (dto == null) return null;

        LeaveTracker leaveTracker = new LeaveTracker();
        leaveTracker.setId(dto.getId());
        leaveTracker.setEmployeeId(dto.getEmployeeId());
        leaveTracker.setTotalLeaves(dto.getTotalLeaves());
        leaveTracker.setUsedLeaves(dto.getUsedLeaves());

        return leaveTracker;
    }

    public List<LeaveTrackerDTO> toLeaveTrackerDTOList(List<LeaveTracker> leaveTrackers) {
        if (leaveTrackers == null) return null;
        return leaveTrackers.stream()
                .map(this::toLeaveTrackerDTO)
                .collect(Collectors.toList());
    }

    // CreatePost mapping methods
    public CreatePostDTO toCreatePostDTO(CreatePost post) {
        if (post == null) return null;

        CreatePostDTO dto = new CreatePostDTO();
        // Map basic fields - assuming CreatePost entity has these fields
        // Note: You may need to adjust these based on your actual CreatePost entity structure
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthorId(post.getAuthorId());
        // Add other mappings as needed based on your CreatePost entity

        return dto;
    }

    public CreatePostDTO toCreatePostDTOWithAuthor(CreatePost post, Employee author) {
        CreatePostDTO dto = toCreatePostDTO(post);
        if (dto != null && author != null) {
            dto.setAuthorName(author.getEmployeeName());
            dto.setAuthorEmail(author.getEmail());
            dto.setAuthorRole(author.getRole());
            dto.setDepartment(author.getDepartment());
        }
        return dto;
    }

    // Compose mapping methods
    public ComposeDTO toComposeDTO(Compose compose) {
        if (compose == null) return null;

        ComposeDTO dto = new ComposeDTO();
        // Map basic fields - assuming Compose entity has these fields
        // Note: You may need to adjust these based on your actual Compose entity structure
        dto.setId(compose.getId());
        dto.setSenderId(compose.getSenderId());
        dto.setRecipientId(compose.getRecipientId());
        dto.setSubject(compose.getSubject());
        dto.setMessage(compose.getMessage());
        // Add other mappings as needed based on your Compose entity

        return dto;
    }

    public ComposeDTO toComposeDTOWithUsers(Compose compose, Employee sender, Employee recipient) {
        ComposeDTO dto = toComposeDTO(compose);
        if (dto != null) {
            if (sender != null) {
                dto.setSenderName(sender.getEmployeeName());
                dto.setSenderEmail(sender.getEmail());
                dto.setSenderRole(sender.getRole());
                dto.setSenderDepartment(sender.getDepartment());
            }
            if (recipient != null) {
                dto.setRecipientName(recipient.getEmployeeName());
                dto.setRecipientEmail(recipient.getEmail());
                dto.setRecipientRole(recipient.getRole());
                dto.setRecipientDepartment(recipient.getDepartment());
            }
        }
        return dto;
    }

    // Utility method for partial updates
    public void updateEmployeeFromDTO(Employee employee, EmployeeDTO dto) {
        if (employee == null || dto == null) return;

        if (dto.getEmployeeName() != null) employee.setEmployeeName(dto.getEmployeeName());
        if (dto.getEmail() != null) employee.setEmail(dto.getEmail());
        if (dto.getGender() != null) employee.setGender(dto.getGender());
        if (dto.getDateOfBirth() != null) employee.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getJoinDate() != null) employee.setJoinDate(dto.getJoinDate());
        if (dto.getMobileNumber() != null) employee.setMobileNumber(dto.getMobileNumber());
        if (dto.getAadhaarNumber() != null) employee.setAadhaarNumber(dto.getAadhaarNumber());
        if (dto.getAccountNumber() != null) employee.setAccountNumber(dto.getAccountNumber());
        if (dto.getDepartment() != null) employee.setDepartment(dto.getDepartment());
        if (dto.getDesignation() != null) employee.setDesignation(dto.getDesignation());
        if (dto.getPreviousCompany() != null) employee.setPreviousCompany(dto.getPreviousCompany());
        if (dto.getPfNumber() != null) employee.setPfNumber(dto.getPfNumber());
        if (dto.getSalary() != null) employee.setSalary(dto.getSalary());
        if (dto.getPermanentAddress() != null) employee.setPermanrntAddress(dto.getPermanentAddress());
        if (dto.getActive() != null) employee.setActive(dto.getActive());
        if (dto.getRole() != null) employee.setRole(dto.getRole());
        // Map address to currentAddress field (primary address field)
        if (dto.getAddress() != null) {
            employee.setCurrentAddress(dto.getAddress());
            employee.setAddress(dto.getAddress()); // Keep both for compatibility
        }
    }

    // Utility method for creating summary DTOs (minimal data for listings)
    public EmployeeDTO toEmployeeSummaryDTO(Employee employee) {
        if (employee == null) return null;

        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setEmployeeName(employee.getEmployeeName());
        dto.setEmail(employee.getEmail());
        dto.setDepartment(employee.getDepartment());
        dto.setDesignation(employee.getDesignation());
        dto.setRole(employee.getRole());
        dto.setActive(employee.isActive());

        return dto;
    }

    public List<EmployeeDTO> toEmployeeSummaryDTOList(List<Employee> employees) {
        if (employees == null) return null;
        return employees.stream()
                .map(this::toEmployeeSummaryDTO)
                .collect(Collectors.toList());
    }
}
