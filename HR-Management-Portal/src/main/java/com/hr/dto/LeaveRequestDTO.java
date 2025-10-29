package com.hr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for LeaveRequest entity
 * Used for data transfer between frontend and backend for leave requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveRequestDTO {

    private Integer id;

    @NotNull(message = "Employee ID is required")
    private Integer employeeId;

    // Employee details for frontend convenience
    private String employeeName;
    private String employeeEmail;
    private String department;
    private String designation;

    @NotBlank(message = "From date is required")
    private String fromDate;

    @NotBlank(message = "To date is required")
    private String toDate;

    private Integer numberOfDays;

    @NotBlank(message = "Leave type is required")
    @Size(max = 50, message = "Leave type cannot exceed 50 characters")
    private String leaveType;

    @NotBlank(message = "Reason is required")
    @Size(max = 1000, message = "Reason cannot exceed 1000 characters")
    private String reason;

    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED, CANCELLED

    private String appliedDate;

    @Size(max = 1000, message = "Approver comments cannot exceed 1000 characters")
    private String approverComments;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDate;
}
