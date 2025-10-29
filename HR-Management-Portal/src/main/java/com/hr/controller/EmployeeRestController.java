package com.hr.controller;

import com.hr.dto.ApiResponse;
import com.hr.dto.EmployeeDTO;
import com.hr.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v2/employees")
@RequiredArgsConstructor
public class EmployeeRestController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeDTO>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "employeeName") String sortBy) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
            Page<EmployeeDTO> employeePage = employeeService.getAllEmployees(pageable);
            
            ApiResponse.PageInfo pageInfo = ApiResponse.PageInfo.builder()
                    .currentPage(page + 1)
                    .totalPages(employeePage.getTotalPages())
                    .totalElements(employeePage.getTotalElements())
                    .pageSize(size)
                    .hasNext(employeePage.hasNext())
                    .hasPrevious(employeePage.hasPrevious())
                    .build();

            ApiResponse<List<EmployeeDTO>> response = ApiResponse.success("Employees retrieved successfully", employeePage.getContent());
            response.setPagination(pageInfo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving employees", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve employees"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> getEmployeeById(@PathVariable Integer id) {
        return employeeService.getEmployeeById(id)
                .map(employee -> ResponseEntity.ok(ApiResponse.success("Employee found", employee)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Employee not found with ID: " + id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeDTO>> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        try {
            EmployeeDTO created = employeeService.createEmployee(employeeDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Employee created successfully", created));
        } catch (Exception e) {
            log.error("Error creating employee", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create employee: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> updateEmployee(
            @PathVariable Integer id, 
            @Valid @RequestBody EmployeeDTO employeeDTO) {
        
        return employeeService.updateEmployee(id, employeeDTO)
                .map(updated -> ResponseEntity.ok(ApiResponse.success("Employee updated successfully", updated)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Employee not found with ID: " + id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Integer id) {
        boolean deleted = employeeService.deleteEmployee(id);
        
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("Employee deactivated successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Employee not found with ID: " + id));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<EmployeeDTO>>> searchEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department) {
        
        try {
            List<EmployeeDTO> employees = employeeService.searchEmployees(name, department);
            return ResponseEntity.ok(ApiResponse.success("Search completed", employees));
        } catch (Exception e) {
            log.error("Error searching employees", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Search failed"));
        }
    }
}
