package com.hr.service;

import com.hr.dto.PayrollDTO;
import com.hr.entity.Employee;
import com.hr.entity.Payroll;
import com.hr.repository.EmployeeRepo;
import com.hr.repository.PayrollRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PayrollService {

    private final PayrollRepo payrollRepo;
    private final EmployeeRepo employeeRepo;

    // Generate payroll for a specific employee for current month
    public Payroll generatePayroll(Integer employeeId) {
        Employee employee = employeeRepo.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        String currentMonth = getCurrentMonth();
        Integer currentYear = getCurrentYear();

        // Check if payroll already exists for this month
        Optional<Payroll> existingPayroll = payrollRepo.findByEmployeeIdAndPayrollMonthAndPayrollYear(
            employeeId, currentMonth, currentYear);

        if (existingPayroll.isPresent()) {
            throw new RuntimeException("Payroll already exists for employee " + employee.getEmployeeName() + 
                " for " + currentMonth + " " + currentYear);
        }

        // Create new payroll
        Payroll payroll = new Payroll();
        payroll.setEmployeeId(employeeId);
        payroll.setBasicSalary(employee.getSalary()); // Using salary from Employee as basic salary
        payroll.setPayrollMonth(currentMonth);
        payroll.setPayrollYear(currentYear);
        payroll.setStatus("DRAFT");

        // Calculate payroll automatically (done in entity)
        payroll.calculatePayroll();

        return payrollRepo.save(payroll);
    }

    // Generate payroll for all active employees
    public List<Payroll> generatePayrollForAllEmployees() {
        List<Employee> activeEmployees = employeeRepo.findAll().stream()
            .filter(Employee::isActive)
            .collect(Collectors.toList());

        List<Payroll> generatedPayrolls = new ArrayList<>();
        String currentMonth = getCurrentMonth();
        Integer currentYear = getCurrentYear();

        for (Employee employee : activeEmployees) {
            try {
                // Check if payroll already exists
                boolean exists = payrollRepo.existsByEmployeeIdAndPayrollMonthAndPayrollYear(
                    employee.getId(), currentMonth, currentYear);

                if (!exists) {
                    Payroll payroll = new Payroll();
                    payroll.setEmployeeId(employee.getId());
                    payroll.setBasicSalary(employee.getSalary());
                    payroll.setPayrollMonth(currentMonth);
                    payroll.setPayrollYear(currentYear);
                    payroll.setStatus("DRAFT");
                    payroll.calculatePayroll();

                    generatedPayrolls.add(payrollRepo.save(payroll));
                }
            } catch (Exception e) {
                System.err.println("Error generating payroll for employee " + employee.getEmployeeName() + ": " + e.getMessage());
            }
        }

        return generatedPayrolls;
    }

    // Get payroll by ID
    public Optional<Payroll> getPayrollById(Integer id) {
        return payrollRepo.findById(id);
    }

    // Get all payrolls for an employee
    public List<Payroll> getPayrollsByEmployeeId(Integer employeeId) {
        return payrollRepo.findByEmployeeId(employeeId);
    }

    // Get payroll for specific employee, month, and year
    public Optional<Payroll> getPayroll(Integer employeeId, String month, Integer year) {
        return payrollRepo.findByEmployeeIdAndPayrollMonthAndPayrollYear(employeeId, month, year);
    }

    // Get all payrolls for current month
    public List<Payroll> getCurrentMonthPayrolls() {
        return payrollRepo.findByPayrollMonthAndPayrollYear(getCurrentMonth(), getCurrentYear());
    }

    // Get all payrolls for a specific month and year
    public List<Payroll> getPayrollsForMonth(String month, Integer year) {
        return payrollRepo.findByPayrollMonthAndPayrollYear(month, year);
    }

    // Update payroll
    public Payroll updatePayroll(Integer id, Payroll updatedPayroll) {
        Payroll existingPayroll = payrollRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Payroll not found with ID: " + id));

        // Update editable fields
        existingPayroll.setBasicSalary(updatedPayroll.getBasicSalary());
        existingPayroll.setOtherAllowances(updatedPayroll.getOtherAllowances());
        existingPayroll.setOtherDeductions(updatedPayroll.getOtherDeductions());
        existingPayroll.setPresentDays(updatedPayroll.getPresentDays());
        existingPayroll.setWorkingDays(updatedPayroll.getWorkingDays());
        existingPayroll.setLeaveDays(updatedPayroll.getLeaveDays());

        // Recalculate payroll
        existingPayroll.calculatePayroll();

        return payrollRepo.save(existingPayroll);
    }

    // Approve payroll
    public Payroll approvePayroll(Integer id) {
        Payroll payroll = payrollRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Payroll not found with ID: " + id));

        payroll.setStatus("APPROVED");
        return payrollRepo.save(payroll);
    }

    // Mark payroll as paid
    public Payroll markPayrollAsPaid(Integer id) {
        Payroll payroll = payrollRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Payroll not found with ID: " + id));

        if (!"APPROVED".equals(payroll.getStatus())) {
            throw new RuntimeException("Payroll must be approved before marking as paid");
        }

        payroll.setStatus("PAID");
        return payrollRepo.save(payroll);
    }

    // Delete payroll
    public void deletePayroll(Integer id) {
        Payroll payroll = payrollRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Payroll not found with ID: " + id));

        if ("PAID".equals(payroll.getStatus())) {
            throw new RuntimeException("Cannot delete payroll that has been paid");
        }

        payrollRepo.deleteById(id);
    }

    // Get payroll statistics
    public Map<String, Object> getPayrollStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Current month stats
        List<Payroll> currentMonthPayrolls = getCurrentMonthPayrolls();
        stats.put("totalEmployees", currentMonthPayrolls.size());
        stats.put("draftPayrolls", currentMonthPayrolls.stream().mapToLong(p -> "DRAFT".equals(p.getStatus()) ? 1 : 0).sum());
        stats.put("approvedPayrolls", currentMonthPayrolls.stream().mapToLong(p -> "APPROVED".equals(p.getStatus()) ? 1 : 0).sum());
        stats.put("paidPayrolls", currentMonthPayrolls.stream().mapToLong(p -> "PAID".equals(p.getStatus()) ? 1 : 0).sum());

        // Calculate total payroll cost
        double totalCost = currentMonthPayrolls.stream()
            .filter(p -> !"DRAFT".equals(p.getStatus()))
            .mapToDouble(Payroll::getNetSalary)
            .sum();
        stats.put("totalPayrollCost", totalCost);

        // Average salary
        OptionalDouble avgSalary = currentMonthPayrolls.stream()
            .mapToDouble(Payroll::getNetSalary)
            .average();
        stats.put("averageSalary", avgSalary.orElse(0.0));

        return stats;
    }

    // Get payroll summary with employee details
    public List<Map<String, Object>> getPayrollSummaryWithEmployeeDetails() {
        List<Payroll> payrolls = getCurrentMonthPayrolls();
        List<Map<String, Object>> summaryList = new ArrayList<>();

        for (Payroll payroll : payrolls) {
            Optional<Employee> employeeOpt = employeeRepo.findById(payroll.getEmployeeId());
            if (employeeOpt.isPresent()) {
                Employee employee = employeeOpt.get();
                Map<String, Object> summary = new HashMap<>();
                
                summary.put("payrollId", payroll.getId());
                summary.put("employeeId", employee.getId());
                summary.put("employeeName", employee.getEmployeeName());
                summary.put("department", employee.getDepartment());
                summary.put("designation", employee.getDesignation());
                summary.put("basicSalary", payroll.getBasicSalary());
                summary.put("grossSalary", payroll.getGrossSalary());
                summary.put("totalDeductions", payroll.getTotalDeductions());
                summary.put("netSalary", payroll.getNetSalary());
                summary.put("status", payroll.getStatus());
                summary.put("presentDays", payroll.getPresentDays());
                summary.put("workingDays", payroll.getWorkingDays());
                summary.put("payrollMonth", payroll.getPayrollMonth());
                summary.put("payrollYear", payroll.getPayrollYear());
                
                summaryList.add(summary);
            }
        }

        return summaryList;
    }

    // Helper methods
    private String getCurrentMonth() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM"));
    }

    private Integer getCurrentYear() {
        return LocalDateTime.now().getYear();
    }

    // Bulk operations
    public List<Payroll> approveAllPayrolls(String month, Integer year) {
        List<Payroll> payrolls = payrollRepo.findByPayrollMonthAndPayrollYear(month, year);
        
        for (Payroll payroll : payrolls) {
            if ("DRAFT".equals(payroll.getStatus())) {
                payroll.setStatus("APPROVED");
            }
        }
        
        return payrollRepo.saveAll(payrolls);
    }

    public List<Payroll> markAllPayrollsAsPaid(String month, Integer year) {
        List<Payroll> payrolls = payrollRepo.findByPayrollMonthAndPayrollYear(month, year);
        
        for (Payroll payroll : payrolls) {
            if ("APPROVED".equals(payroll.getStatus())) {
                payroll.setStatus("PAID");
            }
        }
        
        return payrollRepo.saveAll(payrolls);
    }
}
