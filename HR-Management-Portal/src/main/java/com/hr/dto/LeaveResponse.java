package com.hr.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveResponse {

    private Integer id;
    
    @NotNull
    private Integer employeeId;
    
    // Employee details
    private String employeeName;
    private String department;
    
    @Min(0) @Max(365)
    @Builder.Default
    private Integer totalLeaves = 40;
    
    @Min(0)
    @Builder.Default
    private Integer usedLeaves = 0;
    
    // Computed fields
    private Integer availableLeaves;
    private Double utilizationPercentage;
    private String leaveStatus;
    private Boolean canTakeLeave;
}
