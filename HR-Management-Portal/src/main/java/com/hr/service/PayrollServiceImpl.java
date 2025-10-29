package com.hr.service;

import com.hr.dto.PayrollResponse;
import com.hr.entity.Employee;
import com.hr.entity.Payroll;
import com.hr.repository.EmployeeRepo;
import com.hr.repository.PayrollRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PayrollServiceImpl {

    private final PayrollRepo payrollRepo;
    private final EmployeeRepo employeeRepo;

    public List<PayrollResponse> getAllPayrolls() {
        List<Payroll> payrolls = payrollRepo.findAll();
        return payrolls.stream()
                .map(this::convertToResponse)
                .toList();
    }

    public Optional<PayrollResponse> getPayrollById(Integer id) {
        return payrollRepo.findById(id)
                .map(this::convertToResponse);
    }

    public PayrollResponse createPayroll(Integer employeeId, String month, Integer year) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        Payroll payroll = new Payroll(employeeId, employee.getSalary(), month, year);
        Payroll saved = payrollRepo.save(payroll);
        
        log.info("Created payroll for employee {} for {}/{}", employeeId, month, year);
        return convertToResponse(saved);
    }

    public Optional<PayrollResponse> updatePayrollStatus(Integer id, String status) {
        return payrollRepo.findById(id)
                .map(payroll -> {
                    payroll.setStatus(status);
                    Payroll updated = payrollRepo.save(payroll);
                    log.info("Updated payroll {} status to {}", id, status);
                    return convertToResponse(updated);
                });
    }

    public List<PayrollResponse> getPayrollsByEmployee(Integer employeeId) {
        List<Payroll> payrolls = payrollRepo.findByEmployeeId(employeeId);
        return payrolls.stream()
                .map(this::convertToResponse)
                .toList();
    }

    private PayrollResponse convertToResponse(Payroll payroll) {
        // Get employee details
        Employee employee = employeeRepo.findById(payroll.getEmployeeId()).orElse(null);
        
        PayrollResponse response = PayrollResponse.builder()
                .id(payroll.getId())
                .employeeId(payroll.getEmployeeId())
                .basicSalary(payroll.getBasicSalary())
                .hra(payroll.getHra())
                .transportAllowance(payroll.getTransportAllowance())
                .medicalAllowance(payroll.getMedicalAllowance())
                .pfDeduction(payroll.getPfDeduction())
                .esiDeduction(payroll.getEsiDeduction())
                .grossSalary(payroll.getGrossSalary())
                .totalDeductions(payroll.getTotalDeductions())
                .netSalary(payroll.getNetSalary())
                .payrollMonth(payroll.getPayrollMonth())
                .payrollYear(payroll.getPayrollYear())
                .workingDays(payroll.getWorkingDays())
                .presentDays(payroll.getPresentDays())
                .status(payroll.getStatus())
                .createdDate(payroll.getCreatedDate())
                .build();

        // Set employee details if available
        if (employee != null) {
            response.setEmployeeName(employee.getEmployeeName());
            response.setDepartment(employee.getDepartment());
        }

        // Calculate computed fields
        calculateComputedFields(response);
        return response;
    }

    private void calculateComputedFields(PayrollResponse response) {
        // Calculate attendance percentage
        if (response.getWorkingDays() != null && response.getWorkingDays() > 0) {
            double percentage = (double) response.getPresentDays() / response.getWorkingDays() * 100;
            response.setAttendancePercentage(percentage);
        }

        // Set status label
        response.setStatusLabel(getStatusLabel(response.getStatus()));

        // Set permissions
        response.setCanEdit("DRAFT".equals(response.getStatus()));
    }

    private String getStatusLabel(String status) {
        return switch (status != null ? status.toUpperCase() : "") {
            case "DRAFT" -> "Draft";
            case "APPROVED" -> "Approved";
            case "PAID" -> "Paid";
            default -> status;
        };
    }
}
