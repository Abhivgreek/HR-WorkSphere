package com.hr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayrollDTO {

    private Integer id;

    @NotNull(message = "Employee ID is required")
    private Integer employeeId;

    // Employee details for frontend convenience
    private String employeeName;
    private String employeeEmail;
    private String department;
    private String designation;

    @NotNull(message = "Basic salary is required")
    @Positive(message = "Basic salary must be positive")
    private Double basicSalary;

    @Builder.Default
    private Double hra = 0.0;
    @Builder.Default
    private Double transportAllowance = 0.0;
    @Builder.Default
    private Double medicalAllowance = 0.0;
    @Builder.Default
    private Double otherAllowances = 0.0;

    @Builder.Default
    private Double pfDeduction = 0.0;
    @Builder.Default
    private Double esiDeduction = 0.0;
    @Builder.Default
    private Double professionalTax = 0.0;
    @Builder.Default
    private Double incomeTax = 0.0;
    @Builder.Default
    private Double insuranceDeduction = 0.0;
    @Builder.Default
    private Double otherDeductions = 0.0;

    @Builder.Default
    private Double grossSalary = 0.0;
    @Builder.Default
    private Double totalDeductions = 0.0;
    @Builder.Default
    private Double netSalary = 0.0;

    @NotNull(message = "Payroll month is required")
    private String payrollMonth;

    @NotNull(message = "Payroll year is required")
    @Min(value = 2000, message = "Payroll year must be at least 2000")
    @Max(value = 2100, message = "Payroll year must be at most 2100")
    private Integer payrollYear;

    @Min(value = 1, message = "Working days must be at least 1")
    @Max(value = 31, message = "Working days cannot exceed 31")
    @Builder.Default
    private Integer workingDays = 22;

    @Min(value = 0, message = "Present days cannot be negative")
    @Max(value = 31, message = "Present days cannot exceed 31")
    @Builder.Default
    private Integer presentDays = 22;

    @Min(value = 0, message = "Leave days cannot be negative")
    @Builder.Default
    private Integer leaveDays = 0;

    @Builder.Default
    private String status = "DRAFT"; // DRAFT, APPROVED, PAID

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDate;

    // Essential computed fields only
    private String payrollPeriod;
    private String statusLabel;

    // Constructor with essential fields
    public PayrollDTO(Integer employeeId, Double basicSalary, String payrollMonth, Integer payrollYear) {
        this.employeeId = employeeId;
        this.basicSalary = basicSalary;
        this.payrollMonth = payrollMonth;
        this.payrollYear = payrollYear;
        calculateComputedFields();
    }

    // Calculate essential computed fields only
    private void calculateComputedFields() {
        // Set payroll period
        if (payrollMonth != null && payrollYear != null) {
            this.payrollPeriod = payrollMonth + " " + payrollYear;
        }

        // Set status label
        this.statusLabel = getStatusDisplayText();
    }

    // Custom setters with calculation triggers
    public void updateBasicSalary(Double basicSalary) {
        this.basicSalary = basicSalary;
        calculateComputedFields();
    }

    public void updateHra(Double hra) {
        this.hra = hra;
        calculateComputedFields();
    }

    public void updateTransportAllowance(Double transportAllowance) {
        this.transportAllowance = transportAllowance;
        calculateComputedFields();
    }

    public void updateMedicalAllowance(Double medicalAllowance) {
        this.medicalAllowance = medicalAllowance;
        calculateComputedFields();
    }

    public void updateOtherAllowances(Double otherAllowances) {
        this.otherAllowances = otherAllowances;
        calculateComputedFields();
    }

    public void updatePfDeduction(Double pfDeduction) {
        this.pfDeduction = pfDeduction;
        calculateComputedFields();
    }

    public void updateEsiDeduction(Double esiDeduction) {
        this.esiDeduction = esiDeduction;
        calculateComputedFields();
    }

    public void updateProfessionalTax(Double professionalTax) {
        this.professionalTax = professionalTax;
        calculateComputedFields();
    }

    public void updateIncomeTax(Double incomeTax) {
        this.incomeTax = incomeTax;
        calculateComputedFields();
    }

    public void updateInsuranceDeduction(Double insuranceDeduction) {
        this.insuranceDeduction = insuranceDeduction;
        calculateComputedFields();
    }

    public void updateOtherDeductions(Double otherDeductions) {
        this.otherDeductions = otherDeductions;
        calculateComputedFields();
    }

    public void updateGrossSalary(Double grossSalary) {
        this.grossSalary = grossSalary;
        calculateComputedFields();
    }

    public void updatePayrollMonth(String payrollMonth) {
        this.payrollMonth = payrollMonth;
        calculateComputedFields();
    }

    public void updatePayrollYear(Integer payrollYear) {
        this.payrollYear = payrollYear;
        calculateComputedFields();
    }

    public void updateWorkingDays(Integer workingDays) {
        this.workingDays = workingDays;
        calculateComputedFields();
    }

    public void updatePresentDays(Integer presentDays) {
        this.presentDays = presentDays;
        calculateComputedFields();
    }

    public void updateStatus(String status) {
        this.status = status;
        calculateComputedFields();
    }

    // Utility methods
    public String getFormattedBasicSalary() {
        return basicSalary != null ? String.format("₹%.2f", basicSalary) : "N/A";
    }

    public String getFormattedGrossSalary() {
        return grossSalary != null ? String.format("₹%.2f", grossSalary) : "N/A";
    }

    public String getFormattedNetSalary() {
        return netSalary != null ? String.format("₹%.2f", netSalary) : "N/A";
    }

    public String getFormattedTotalDeductions() {
        return totalDeductions != null ? String.format("₹%.2f", totalDeductions) : "N/A";
    }


    private String getStatusDisplayText() {
        if (status == null) return "Unknown";
        switch (status.toUpperCase()) {
            case "DRAFT": return "Draft";
            case "APPROVED": return "Approved";
            case "PAID": return "Paid";
            default: return status;
        }
    }

    public boolean isPaid() {
        return "PAID".equalsIgnoreCase(status);
    }

    public boolean isApproved() {
        return "APPROVED".equalsIgnoreCase(status);
    }

    public boolean isDraft() {
        return "DRAFT".equalsIgnoreCase(status);
    }

}
