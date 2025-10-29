package com.hr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for LeaveTracker entity
 * Used for data transfer between frontend and backend
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveTrackerDTO {

    private Integer id;

    @NotNull(message = "Employee ID is required")
    private Integer employeeId;

    // Employee details for frontend convenience
    private String employeeName;
    private String employeeEmail;
    private String department;
    private String designation;

    @Min(value = 0, message = "Total leaves cannot be negative")
    @Max(value = 365, message = "Total leaves cannot exceed 365")
    @Builder.Default
    private Integer totalLeaves = 40;

    @Min(value = 0, message = "Used leaves cannot be negative")
    @Builder.Default
    private Integer usedLeaves = 0;

    // Additional fields to match LeaveTracker entity
    private String fromDate;
    private String toDate;
    private Integer numberOfDays;
    private String leaveType;
    private String reason;
    @Builder.Default
    private String status = "PENDING";
    private String appliedDate;
    private String approverComments;
    private java.time.LocalDateTime createdDate;
    private java.time.LocalDateTime updatedDate;

    // Essential computed fields for dashboard
    private Integer remainingLeaves;
    private Integer availableLeaves;

    // Custom constructors for convenience
    public LeaveTrackerDTO(Integer employeeId, Integer totalLeaves) {
        this.employeeId = employeeId;
        this.totalLeaves = totalLeaves;
        this.usedLeaves = 0;
        this.status = "PENDING";
        calculateComputedFields();
    }

    public LeaveTrackerDTO(Integer employeeId, Integer totalLeaves, Integer usedLeaves) {
        this.employeeId = employeeId;
        this.totalLeaves = totalLeaves;
        this.usedLeaves = usedLeaves;
        this.status = "PENDING";
        calculateComputedFields();
    }

    // Calculate essential computed fields only
    private void calculateComputedFields() {
        // Calculate available/remaining leaves
        if (totalLeaves != null && usedLeaves != null) {
            this.availableLeaves = totalLeaves - usedLeaves;
            this.remainingLeaves = this.availableLeaves;
        }
    }


    // Utility methods to trigger recalculation when needed
    public void updateEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
        calculateComputedFields();
    }
    
    public void updateTotalLeaves(Integer totalLeaves) {
        this.totalLeaves = totalLeaves;
        calculateComputedFields();
    }
    
    public void updateUsedLeaves(Integer usedLeaves) {
        this.usedLeaves = usedLeaves;
        calculateComputedFields();
    }

    // Essential utility methods

    public boolean hasExceededLeaves() {
        return usedLeaves != null && totalLeaves != null && usedLeaves > totalLeaves;
    }

    public boolean isLeaveExhausted() {
        return availableLeaves != null && availableLeaves <= 0;
    }

    public boolean isLowOnLeaves() {
        return availableLeaves != null && totalLeaves != null && 
               availableLeaves <= (totalLeaves * 0.1); // Less than 10% remaining
    }

    public boolean canTakeLeaves(int requestedDays) {
        return availableLeaves != null && availableLeaves >= requestedDays;
    }

    public Integer getLeaveDeficit() {
        if (usedLeaves != null && totalLeaves != null && usedLeaves > totalLeaves) {
            return usedLeaves - totalLeaves;
        }
        return 0;
    }

    // Method to simulate taking leaves
    public boolean takeLeaves(int days) {
        if (!canTakeLeaves(days)) {
            return false;
        }
        this.usedLeaves += days;
        calculateComputedFields();
        return true;
    }

    // Method to simulate restoring leaves (if leave is cancelled)
    public void restoreLeaves(int days) {
        if (this.usedLeaves >= days) {
            this.usedLeaves -= days;
            calculateComputedFields();
        }
    }

    // Method to reset leaves (typically at year end)
    public void resetLeaves() {
        this.usedLeaves = 0;
        calculateComputedFields();
    }

    // Method to adjust total leaves (admin function)
    public void adjustTotalLeaves(int newTotal) {
        this.totalLeaves = newTotal;
        calculateComputedFields();
    }

}
