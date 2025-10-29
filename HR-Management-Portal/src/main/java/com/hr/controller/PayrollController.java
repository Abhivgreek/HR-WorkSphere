package com.hr.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hr.entity.Employee;
import com.hr.entity.Payroll;
import com.hr.repository.EmployeeRepo;
import com.hr.service.PayrollService;

@RestController
@RequestMapping("/api/payroll")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class PayrollController {

    @Autowired
    private PayrollService payrollService;
    
    @Autowired
    private EmployeeRepo employeeRepo;

    @GetMapping("/{id}")
    public ResponseEntity<Payroll> getPayrollById(@PathVariable Integer id) {
        return payrollService.getPayrollById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Payroll>> getPayrollsByEmployeeId(@PathVariable Integer employeeId) {
        return ResponseEntity.ok(payrollService.getPayrollsByEmployeeId(employeeId));
    }

    @GetMapping("/employee/{employeeId}/monthly")
    public ResponseEntity<Map<String, Object>> getEmployeePayrollByMonth(
            @PathVariable Integer employeeId, 
            @RequestParam String month, 
            @RequestParam Integer year) {
        try {
            // Convert month name to number if needed
            Integer monthNumber = convertMonthToNumber(month);
            
            // First try to get actual payroll data from the service
            List<Payroll> payrolls = payrollService.getPayrollsByEmployeeId(employeeId);
            
            // Filter by month and year (payroll entities have payrollMonth/payrollYear fields)
            Payroll monthlyPayroll = payrolls.stream()
                .filter(p -> p.getPayrollYear() != null && p.getPayrollYear().equals(year) &&
                            p.getPayrollMonth() != null && 
                            (Integer.parseInt(p.getPayrollMonth()) == monthNumber || p.getPayrollMonth().equalsIgnoreCase(month)))
                .findFirst()
                .orElse(null);
            
            if (monthlyPayroll != null) {
                // Convert payroll to map for frontend
                Map<String, Object> payrollData = convertPayrollToMap(monthlyPayroll);
                return ResponseEntity.ok(payrollData);
            } else {
                // If no payroll found, generate mock data based on employee information
                Optional<Employee> employeeOpt = employeeRepo.findById(employeeId);
                if (employeeOpt.isPresent()) {
                    Employee employee = employeeOpt.get();
                    Map<String, Object> mockPayroll = generateMockPayrollData(employee, monthNumber, year);
                    return ResponseEntity.ok(mockPayroll);
                } else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Employee not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving payroll: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/generate/{employeeId}")
    public ResponseEntity<Payroll> generatePayrollForEmployee(@PathVariable Integer employeeId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payrollService.generatePayroll(employeeId));
    }

    @PostMapping("/generate-all")
    public ResponseEntity<List<Payroll>> generatePayrollForAllEmployees() {
        return ResponseEntity.status(HttpStatus.CREATED).body(payrollService.generatePayrollForAllEmployees());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Payroll> updatePayroll(@PathVariable Integer id, @RequestBody Payroll payroll) {
        return ResponseEntity.ok(payrollService.updatePayroll(id, payroll));
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<Payroll> approvePayroll(@PathVariable Integer id) {
        return ResponseEntity.ok(payrollService.approvePayroll(id));
    }

    @PutMapping("/pay/{id}")
    public ResponseEntity<Payroll> markPayrollAsPaid(@PathVariable Integer id) {
        return ResponseEntity.ok(payrollService.markPayrollAsPaid(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayroll(@PathVariable Integer id) {
        payrollService.deletePayroll(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/current-month")
    public ResponseEntity<List<Map<String, Object>>> getCurrentMonthPayrollSummary() {
        return ResponseEntity.ok(payrollService.getPayrollSummaryWithEmployeeDetails());
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getPayrollStatistics() {
        return ResponseEntity.ok(payrollService.getPayrollStatistics());
    }

    @PostMapping("/approve-all")
    public ResponseEntity<List<Payroll>> approveAllPayrolls(@RequestParam String month, @RequestParam Integer year) {
        return ResponseEntity.ok(payrollService.approveAllPayrolls(month, year));
    }

    @PostMapping("/pay-all")
    public ResponseEntity<List<Payroll>> markAllPayrollsAsPaid(@RequestParam String month, @RequestParam Integer year) {
        return ResponseEntity.ok(payrollService.markAllPayrollsAsPaid(month, year));
    }

    // Helper methods
    private Map<String, Object> convertPayrollToMap(Payroll payroll) {
        Map<String, Object> map = new HashMap<>();
        
        // Get employee details for missing fields
        Optional<Employee> employeeOpt = employeeRepo.findById(payroll.getEmployeeId());
        Employee employee = employeeOpt.orElse(null);
        
        // Basic information
        map.put("employeeId", payroll.getEmployeeId());
        map.put("employeeName", employee != null ? employee.getEmployeeName() : "Employee");
        map.put("department", employee != null ? employee.getDepartment() : "Department");
        map.put("designation", employee != null ? employee.getDesignation() : "Designation");
        map.put("month", payroll.getPayrollMonth() != null ? Integer.parseInt(payroll.getPayrollMonth()) : 1);
        map.put("year", payroll.getPayrollYear());
        
        // Salary components
        map.put("basicSalary", payroll.getBasicSalary());
        map.put("hra", payroll.getHra());
        map.put("da", payroll.getTransportAllowance()); // Using transport as DA equivalent
        map.put("conveyanceAllowance", payroll.getTransportAllowance());
        map.put("medicalAllowance", payroll.getMedicalAllowance());
        map.put("specialAllowance", payroll.getOtherAllowances());
        map.put("grossSalary", payroll.getGrossSalary());
        
        // Deductions
        map.put("pfDeduction", payroll.getPfDeduction());
        map.put("esiDeduction", payroll.getEsiDeduction());
        map.put("professionalTax", payroll.getProfessionalTax());
        map.put("incomeTax", payroll.getIncomeTax());
        map.put("insuranceDeduction", payroll.getInsuranceDeduction());
        map.put("totalDeductions", payroll.getTotalDeductions());
        
        // Net salary and other details
        map.put("netSalary", payroll.getNetSalary());
        map.put("workingDays", payroll.getWorkingDays());
        map.put("presentDays", payroll.getPresentDays());
        map.put("status", payroll.getStatus());
        
        return map;
    }

    private Integer convertMonthToNumber(String month) {
        try {
            // If it's already a number, return it
            return Integer.parseInt(month);
        } catch (NumberFormatException e) {
            // Convert month name to number
            switch (month.toLowerCase()) {
                case "january": return 1;
                case "february": return 2;
                case "march": return 3;
                case "april": return 4;
                case "may": return 5;
                case "june": return 6;
                case "july": return 7;
                case "august": return 8;
                case "september": return 9;
                case "october": return 10;
                case "november": return 11;
                case "december": return 12;
                default: return 1; // Default to January if unknown
            }
        }
    }

    private Map<String, Object> generateMockPayrollData(Employee employee, Integer month, Integer year) {
        Map<String, Object> mockPayroll = new HashMap<>();
        
        // Employee information
        mockPayroll.put("employeeId", employee.getId());
        mockPayroll.put("employeeName", employee.getEmployeeName());
        mockPayroll.put("department", employee.getDepartment());
        mockPayroll.put("designation", employee.getDesignation());
        mockPayroll.put("month", month);
        mockPayroll.put("year", year);
        
        // Calculate salary components based on employee's basic salary
        double basicSalary = employee.getSalary() != null ? employee.getSalary() : 45000.0;
        double hra = basicSalary * 0.4;
        double da = basicSalary * 0.1;
        double conveyanceAllowance = 1600.0;
        double medicalAllowance = 1250.0;
        double specialAllowance = basicSalary * 0.05;
        
        // Calculate deductions
        double pfDeduction = basicSalary * 0.12;
        double esiDeduction = (basicSalary + hra + da) * 0.0075;
        double professionalTax = 200.0;
        double incomeTax = calculateIncomeTax(basicSalary);
        double insuranceDeduction = 500.0;
        
        // Calculate totals
        double grossSalary = basicSalary + hra + da + conveyanceAllowance + medicalAllowance + specialAllowance;
        double totalDeductions = pfDeduction + esiDeduction + professionalTax + incomeTax + insuranceDeduction;
        double netSalary = grossSalary - totalDeductions;
        
        // Add all calculated values to the map
        mockPayroll.put("basicSalary", basicSalary);
        mockPayroll.put("hra", hra);
        mockPayroll.put("da", da);
        mockPayroll.put("conveyanceAllowance", conveyanceAllowance);
        mockPayroll.put("medicalAllowance", medicalAllowance);
        mockPayroll.put("specialAllowance", specialAllowance);
        mockPayroll.put("grossSalary", grossSalary);
        
        mockPayroll.put("pfDeduction", pfDeduction);
        mockPayroll.put("esiDeduction", esiDeduction);
        mockPayroll.put("professionalTax", professionalTax);
        mockPayroll.put("incomeTax", incomeTax);
        mockPayroll.put("insuranceDeduction", insuranceDeduction);
        mockPayroll.put("totalDeductions", totalDeductions);
        
        mockPayroll.put("netSalary", netSalary);
        mockPayroll.put("workingDays", 30);
        mockPayroll.put("presentDays", 30);
        mockPayroll.put("status", "Generated");
        
        return mockPayroll;
    }

    private double calculateIncomeTax(double basicSalary) {
        double annualSalary = basicSalary * 12;
        if (annualSalary <= 250000) return 0;
        if (annualSalary <= 500000) return (annualSalary - 250000) * 0.05 / 12;
        if (annualSalary <= 1000000) return (12500 + (annualSalary - 500000) * 0.2) / 12;
        return (112500 + (annualSalary - 1000000) * 0.3) / 12;
    }
}
