package com.hr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollResponse {

    private Integer id;
    private Integer employeeId;
    
    // Employee details
    private String employeeName;
    private String department;
    
    @DecimalMin(value = "0.0")
    private Double basicSalary;
    
    @Builder.Default
    private Double hra = 0.0;
    
    @Builder.Default
    private Double transportAllowance = 0.0;
    
    @Builder.Default
    private Double medicalAllowance = 0.0;
    
    @Builder.Default
    private Double pfDeduction = 0.0;
    
    @Builder.Default
    private Double esiDeduction = 0.0;
    
    private Double grossSalary;
    private Double totalDeductions;
    private Double netSalary;
    
    private String payrollMonth;
    
    @Min(2000) @Max(2100)
    private Integer payrollYear;
    
    @Min(1) @Max(31)
    @Builder.Default
    private Integer workingDays = 22;
    
    @Min(0) @Max(31)
    @Builder.Default
    private Integer presentDays = 22;
    
    @Builder.Default
    private String status = "DRAFT";
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    
    // Computed fields
    private Double attendancePercentage;
    private String statusLabel;
    private Boolean canEdit;
}
