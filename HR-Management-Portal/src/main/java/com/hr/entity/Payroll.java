package com.hr.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "PAYROLL")
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "EMPLOYEE_ID")
    private Integer employeeId;

    @NotNull
    @Column(name = "BASIC_SALARY")
    private Double basicSalary;

    @Column(name = "HRA")
    private Double hra = 0.0;

    @Column(name = "TRANSPORT_ALLOWANCE")
    private Double transportAllowance = 0.0;

    @Column(name = "MEDICAL_ALLOWANCE")
    private Double medicalAllowance = 0.0;

    @Column(name = "OTHER_ALLOWANCES")
    private Double otherAllowances = 0.0;

    @Column(name = "PF_DEDUCTION")
    private Double pfDeduction = 0.0;

    @Column(name = "ESI_DEDUCTION")
    private Double esiDeduction = 0.0;

    @Column(name = "PROFESSIONAL_TAX")
    private Double professionalTax = 0.0;

    @Column(name = "INCOME_TAX")
    private Double incomeTax = 0.0;

    @Column(name = "INSURANCE_DEDUCTION")
    private Double insuranceDeduction = 0.0;

    @Column(name = "OTHER_DEDUCTIONS")
    private Double otherDeductions = 0.0;

    @Column(name = "GROSS_SALARY")
    private Double grossSalary = 0.0;

    @Column(name = "TOTAL_DEDUCTIONS")
    private Double totalDeductions = 0.0;

    @Column(name = "NET_SALARY")
    private Double netSalary = 0.0;

    @Column(name = "PAYROLL_MONTH")
    private String payrollMonth;

    @Column(name = "PAYROLL_YEAR")
    private Integer payrollYear;

    @Column(name = "WORKING_DAYS")
    private Integer workingDays = 22;

    @Column(name = "PRESENT_DAYS")
    private Integer presentDays = 22;

    @Column(name = "LEAVE_DAYS")
    private Integer leaveDays = 0;

    @Column(name = "STATUS")
    private String status = "DRAFT"; // DRAFT, APPROVED, PAID

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

    // Constructors
    public Payroll() {
        super();
    }

    public Payroll(Integer employeeId, Double basicSalary, String payrollMonth, Integer payrollYear) {
        this.employeeId = employeeId;
        this.basicSalary = basicSalary;
        this.payrollMonth = payrollMonth;
        this.payrollYear = payrollYear;
        calculatePayroll();
    }

    // Calculate payroll automatically
    public void calculatePayroll() {
        if (basicSalary != null && basicSalary > 0) {
            // Calculate allowances
            this.hra = basicSalary * 0.40; // 40% HRA
            this.transportAllowance = 2000.0; // Fixed transport allowance
            this.medicalAllowance = 1500.0; // Fixed medical allowance
            
            // Calculate gross salary
            this.grossSalary = basicSalary + hra + transportAllowance + medicalAllowance + otherAllowances;
            
            // Calculate deductions
            this.pfDeduction = grossSalary * 0.12; // 12% PF
            this.esiDeduction = grossSalary * 0.0175; // 1.75% ESI
            this.professionalTax = 200.0; // Fixed professional tax
            this.insuranceDeduction = grossSalary * 0.02; // 2% insurance
            
            // Calculate income tax (simplified progressive tax)
            this.incomeTax = calculateIncomeTax(grossSalary * 12); // Annual salary
            
            // Total deductions
            this.totalDeductions = pfDeduction + esiDeduction + professionalTax + 
                                 incomeTax + insuranceDeduction + otherDeductions;
            
            // Net salary with attendance adjustment
            double attendanceRatio = (double) presentDays / workingDays;
            this.netSalary = (grossSalary - totalDeductions) * attendanceRatio;
        }
    }

    private Double calculateIncomeTax(Double annualSalary) {
        // Simplified income tax calculation (Indian tax slabs)
        double tax = 0.0;
        if (annualSalary > 250000) {
            if (annualSalary <= 500000) {
                tax = (annualSalary - 250000) * 0.05;
            } else if (annualSalary <= 1000000) {
                tax = 12500 + (annualSalary - 500000) * 0.20;
            } else {
                tax = 112500 + (annualSalary - 1000000) * 0.30;
            }
        }
        return tax / 12; // Monthly tax
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(Double basicSalary) {
        this.basicSalary = basicSalary;
        calculatePayroll(); // Recalculate when basic salary changes
    }

    public Double getHra() {
        return hra;
    }

    public void setHra(Double hra) {
        this.hra = hra;
    }

    public Double getTransportAllowance() {
        return transportAllowance;
    }

    public void setTransportAllowance(Double transportAllowance) {
        this.transportAllowance = transportAllowance;
    }

    public Double getMedicalAllowance() {
        return medicalAllowance;
    }

    public void setMedicalAllowance(Double medicalAllowance) {
        this.medicalAllowance = medicalAllowance;
    }

    public Double getOtherAllowances() {
        return otherAllowances;
    }

    public void setOtherAllowances(Double otherAllowances) {
        this.otherAllowances = otherAllowances;
    }

    public Double getPfDeduction() {
        return pfDeduction;
    }

    public void setPfDeduction(Double pfDeduction) {
        this.pfDeduction = pfDeduction;
    }

    public Double getEsiDeduction() {
        return esiDeduction;
    }

    public void setEsiDeduction(Double esiDeduction) {
        this.esiDeduction = esiDeduction;
    }

    public Double getProfessionalTax() {
        return professionalTax;
    }

    public void setProfessionalTax(Double professionalTax) {
        this.professionalTax = professionalTax;
    }

    public Double getIncomeTax() {
        return incomeTax;
    }

    public void setIncomeTax(Double incomeTax) {
        this.incomeTax = incomeTax;
    }

    public Double getInsuranceDeduction() {
        return insuranceDeduction;
    }

    public void setInsuranceDeduction(Double insuranceDeduction) {
        this.insuranceDeduction = insuranceDeduction;
    }

    public Double getOtherDeductions() {
        return otherDeductions;
    }

    public void setOtherDeductions(Double otherDeductions) {
        this.otherDeductions = otherDeductions;
    }

    public Double getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(Double grossSalary) {
        this.grossSalary = grossSalary;
    }

    public Double getTotalDeductions() {
        return totalDeductions;
    }

    public void setTotalDeductions(Double totalDeductions) {
        this.totalDeductions = totalDeductions;
    }

    public Double getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(Double netSalary) {
        this.netSalary = netSalary;
    }

    public String getPayrollMonth() {
        return payrollMonth;
    }

    public void setPayrollMonth(String payrollMonth) {
        this.payrollMonth = payrollMonth;
    }

    public Integer getPayrollYear() {
        return payrollYear;
    }

    public void setPayrollYear(Integer payrollYear) {
        this.payrollYear = payrollYear;
    }

    public Integer getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(Integer workingDays) {
        this.workingDays = workingDays;
    }

    public Integer getPresentDays() {
        return presentDays;
    }

    public void setPresentDays(Integer presentDays) {
        this.presentDays = presentDays;
        calculatePayroll(); // Recalculate when attendance changes
    }

    public Integer getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(Integer leaveDays) {
        this.leaveDays = leaveDays;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public String toString() {
        return "Payroll [id=" + id + ", employeeId=" + employeeId + ", basicSalary=" + basicSalary
                + ", grossSalary=" + grossSalary + ", netSalary=" + netSalary + ", payrollMonth=" + payrollMonth
                + ", payrollYear=" + payrollYear + ", status=" + status + "]";
    }
}
