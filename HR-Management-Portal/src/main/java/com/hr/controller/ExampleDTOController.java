package com.hr.controller;

import com.hr.dto.*;
import com.hr.entity.Employee;
import com.hr.entity.Payroll;
import com.hr.entity.LeaveTracker;
import com.hr.repository.EmployeeRepo;
import com.hr.repository.PayrollRepo;
import com.hr.repository.LeaveTrackerRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Example Controller demonstrating proper DTO usage
 * This shows best practices for using DTOs in Spring Boot applications
 */
@RestController
@RequestMapping("/api/examples")
@Validated
public class ExampleDTOController {

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private PayrollRepo payrollRepo;

    @Autowired
    private LeaveTrackerRepo leaveTrackerRepo;

    @Autowired
    private DtoMapper dtoMapper;

    /**
     * Example 1: Get all employees with pagination
     * Returns: ApiResponseDTO<List<EmployeeDTO>> with pagination info
     */
    @GetMapping("/employees")
    public ResponseEntity<ApiResponseDTO<List<EmployeeDTO>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "employeeName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Employee> employeePage = employeeRepo.findAll(pageable);

            List<EmployeeDTO> employeeDTOs = dtoMapper.toEmployeeDTOList(employeePage.getContent());

            ApiResponseDTO.PaginationDTO pagination = new ApiResponseDTO.PaginationDTO(
                employeePage.getNumber() + 1, // Convert to 1-based
                employeePage.getTotalPages(),
                employeePage.getTotalElements(),
                employeePage.getSize()
            );

            ApiResponseDTO<List<EmployeeDTO>> response = ApiResponseDTO
                .success("Employees retrieved successfully", employeeDTOs)
                .withPagination(pagination);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponseDTO<List<EmployeeDTO>> response = ApiResponseDTO
                .internalServerError("Error retrieving employees: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Example 2: Get employee by ID
     * Returns: ApiResponseDTO<EmployeeDTO>
     */
    @GetMapping("/employees/{id}")
    public ResponseEntity<ApiResponseDTO<EmployeeDTO>> getEmployeeById(@PathVariable Integer id) {
        try {
            Optional<Employee> employeeOpt = employeeRepo.findById(id);
            
            if (employeeOpt.isEmpty()) {
                ApiResponseDTO<EmployeeDTO> response = ApiResponseDTO
                    .notFound("Employee not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            EmployeeDTO employeeDTO = dtoMapper.toEmployeeDTO(employeeOpt.get());
            
            ApiResponseDTO<EmployeeDTO> response = ApiResponseDTO
                .success("Employee retrieved successfully", employeeDTO);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponseDTO<EmployeeDTO> response = ApiResponseDTO
                .internalServerError("Error retrieving employee: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Example 3: Create new employee
     * Accepts: EmployeeDTO in request body
     * Returns: ApiResponseDTO<EmployeeDTO>
     */
    @PostMapping("/employees")
    public ResponseEntity<ApiResponseDTO<EmployeeDTO>> createEmployee(
            @Valid @RequestBody EmployeeDTO employeeDTO) {
        
        try {
            // Check if email already exists
            if (employeeRepo.findByEmail(employeeDTO.getEmail()) != null) {
                ApiResponseDTO<EmployeeDTO> response = ApiResponseDTO
                    .error("Employee with this email already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            Employee employee = dtoMapper.toEmployee(employeeDTO);
            Employee savedEmployee = employeeRepo.save(employee);
            EmployeeDTO savedEmployeeDTO = dtoMapper.toEmployeeDTO(savedEmployee);

            ApiResponseDTO<EmployeeDTO> response = ApiResponseDTO
                .created("Employee created successfully", savedEmployeeDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            ApiResponseDTO<EmployeeDTO> response = ApiResponseDTO
                .internalServerError("Error creating employee: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Example 4: Update employee
     * Accepts: EmployeeDTO in request body
     * Returns: ApiResponseDTO<EmployeeDTO>
     */
    @PutMapping("/employees/{id}")
    public ResponseEntity<ApiResponseDTO<EmployeeDTO>> updateEmployee(
            @PathVariable Integer id, 
            @Valid @RequestBody EmployeeDTO employeeDTO) {
        
        try {
            Optional<Employee> employeeOpt = employeeRepo.findById(id);
            
            if (employeeOpt.isEmpty()) {
                ApiResponseDTO<EmployeeDTO> response = ApiResponseDTO
                    .notFound("Employee not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Employee employee = employeeOpt.get();
            dtoMapper.updateEmployeeFromDTO(employee, employeeDTO);
            Employee updatedEmployee = employeeRepo.save(employee);
            EmployeeDTO updatedEmployeeDTO = dtoMapper.toEmployeeDTO(updatedEmployee);

            ApiResponseDTO<EmployeeDTO> response = ApiResponseDTO
                .success("Employee updated successfully", updatedEmployeeDTO);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponseDTO<EmployeeDTO> response = ApiResponseDTO
                .internalServerError("Error updating employee: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Example 5: Get payroll with employee details
     * Demonstrates joining data from multiple entities
     * Returns: ApiResponseDTO<List<PayrollDTO>>
     */
    @GetMapping("/payroll")
    public ResponseEntity<ApiResponseDTO<List<PayrollDTO>>> getAllPayrollWithEmployees() {
        try {
            List<Payroll> payrolls = payrollRepo.findAll();
            List<PayrollDTO> payrollDTOs = payrolls.stream().map(payroll -> {
                Employee employee = employeeRepo.findById(payroll.getEmployeeId()).orElse(null);
                return dtoMapper.toPayrollDTOWithEmployee(payroll, employee);
            }).toList();

            ApiResponseDTO<List<PayrollDTO>> response = ApiResponseDTO
                .success("Payroll data retrieved successfully", payrollDTOs);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponseDTO<List<PayrollDTO>> response = ApiResponseDTO
                .internalServerError("Error retrieving payroll data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Example 6: Search employees
     * Demonstrates filtering and search functionality
     * Returns: ApiResponseDTO<List<EmployeeDTO>>
     */
    @GetMapping("/employees/search")
    public ResponseEntity<ApiResponseDTO<List<EmployeeDTO>>> searchEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) Boolean active) {
        
        try {
            // This is a simplified search - in real applications, use Specifications or custom queries
            List<Employee> employees = employeeRepo.findAll().stream()
                .filter(emp -> name == null || emp.getEmployeeName().toLowerCase().contains(name.toLowerCase()))
                .filter(emp -> department == null || emp.getDepartment().equalsIgnoreCase(department))
                .filter(emp -> designation == null || emp.getDesignation().equalsIgnoreCase(designation))
                .filter(emp -> active == null || emp.isActive() == active)
                .toList();

            List<EmployeeDTO> employeeDTOs = dtoMapper.toEmployeeDTOList(employees);

            ApiResponseDTO<List<EmployeeDTO>> response = ApiResponseDTO
                .success("Search completed successfully", employeeDTOs)
                .withMeta(new SearchMetaData(employees.size()));
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponseDTO<List<EmployeeDTO>> response = ApiResponseDTO
                .internalServerError("Error searching employees: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Example 7: Get employee summary (minimal data for dropdowns/lists)
     * Returns: ApiResponseDTO<List<EmployeeDTO>>
     */
    @GetMapping("/employees/summary")
    public ResponseEntity<ApiResponseDTO<List<EmployeeDTO>>> getEmployeeSummary() {
        try {
            List<Employee> employees = employeeRepo.findByActiveTrue();
            List<EmployeeDTO> summaryDTOs = dtoMapper.toEmployeeSummaryDTOList(employees);

            ApiResponseDTO<List<EmployeeDTO>> response = ApiResponseDTO
                .success("Employee summary retrieved successfully", summaryDTOs);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponseDTO<List<EmployeeDTO>> response = ApiResponseDTO
                .internalServerError("Error retrieving employee summary: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Example 8: Get leave tracker with employee details
     * Returns: ApiResponseDTO<List<LeaveTrackerDTO>>
     */
    @GetMapping("/leave-tracker")
    public ResponseEntity<ApiResponseDTO<List<LeaveTrackerDTO>>> getLeaveTrackerWithEmployees() {
        try {
            List<LeaveTracker> leaveTrackers = leaveTrackerRepo.findAll();
            List<LeaveTrackerDTO> leaveTrackerDTOs = leaveTrackers.stream().map(tracker -> {
                Employee employee = employeeRepo.findById(tracker.getEmployeeId()).orElse(null);
                return dtoMapper.toLeaveTrackerDTOWithEmployee(tracker, employee);
            }).toList();

            ApiResponseDTO<List<LeaveTrackerDTO>> response = ApiResponseDTO
                .success("Leave tracker data retrieved successfully", leaveTrackerDTOs);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponseDTO<List<LeaveTrackerDTO>> response = ApiResponseDTO
                .internalServerError("Error retrieving leave tracker data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Example 9: Delete employee (soft delete)
     * Returns: ApiResponseDTO<String>
     */
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<ApiResponseDTO<String>> deleteEmployee(@PathVariable Integer id) {
        try {
            Optional<Employee> employeeOpt = employeeRepo.findById(id);
            
            if (employeeOpt.isEmpty()) {
                ApiResponseDTO<String> response = ApiResponseDTO
                    .notFound("Employee not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Employee employee = employeeOpt.get();
            employee.setActive(false); // Soft delete
            employeeRepo.save(employee);

            ApiResponseDTO<String> response = ApiResponseDTO
                .success("Employee deactivated successfully");
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponseDTO<String> response = ApiResponseDTO
                .internalServerError("Error deleting employee: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Example 10: Bulk operations
     * Returns: ApiResponseDTO<BulkOperationResult>
     */
    @PostMapping("/employees/bulk-activate")
    public ResponseEntity<ApiResponseDTO<BulkOperationResult>> bulkActivateEmployees(
            @RequestBody List<Integer> employeeIds) {
        
        try {
            List<Employee> employees = employeeRepo.findAllById(employeeIds);
            
            int updatedCount = 0;
            for (Employee employee : employees) {
                if (!employee.isActive()) {
                    employee.setActive(true);
                    updatedCount++;
                }
            }
            
            employeeRepo.saveAll(employees);

            BulkOperationResult result = new BulkOperationResult(
                employeeIds.size(), 
                updatedCount, 
                employeeIds.size() - employees.size()
            );

            ApiResponseDTO<BulkOperationResult> response = ApiResponseDTO
                .success("Bulk activation completed", result);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponseDTO<BulkOperationResult> response = ApiResponseDTO
                .internalServerError("Error in bulk activation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Helper classes for meta data
    private static class SearchMetaData {
        private final int resultCount;

        public SearchMetaData(int resultCount) {
            this.resultCount = resultCount;
        }

        public int getResultCount() {
            return resultCount;
        }
    }

    private static class BulkOperationResult {
        private final int requested;
        private final int updated;
        private final int notFound;

        public BulkOperationResult(int requested, int updated, int notFound) {
            this.requested = requested;
            this.updated = updated;
            this.notFound = notFound;
        }

        public int getRequested() { return requested; }
        public int getUpdated() { return updated; }
        public int getNotFound() { return notFound; }
    }
}
