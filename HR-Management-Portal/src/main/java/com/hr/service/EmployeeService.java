package com.hr.service;

import com.hr.dto.EmployeeDTO;
import com.hr.entity.Employee;
import com.hr.repository.EmployeeRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepo employeeRepo;

    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> employees = employeeRepo.findAll();
        return employees.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Page<EmployeeDTO> getAllEmployees(Pageable pageable) {
        Page<Employee> employees = employeeRepo.findAll(pageable);
        return employees.map(this::convertToDTO);
    }

    public Optional<EmployeeDTO> getEmployeeById(Integer id) {
        return employeeRepo.findById(id)
                .map(this::convertToDTO);
    }

    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = convertToEntity(employeeDTO);
        Employee saved = employeeRepo.save(employee);
        log.info("Created employee with ID: {}", saved.getId());
        return convertToDTO(saved);
    }

    public Optional<EmployeeDTO> updateEmployee(Integer id, EmployeeDTO employeeDTO) {
        return employeeRepo.findById(id)
                .map(existing -> {
                    updateEntityFromDTO(existing, employeeDTO);
                    Employee updated = employeeRepo.save(existing);
                    log.info("Updated employee with ID: {}", updated.getId());
                    return convertToDTO(updated);
                });
    }

    public boolean deleteEmployee(Integer id) {
        return employeeRepo.findById(id)
                .map(employee -> {
                    employee.setActive(false); // Soft delete
                    employeeRepo.save(employee);
                    log.info("Deactivated employee with ID: {}", id);
                    return true;
                })
                .orElse(false);
    }

    public List<EmployeeDTO> searchEmployees(String name, String department) {
        List<Employee> employees = employeeRepo.findAll().stream()
                .filter(emp -> name == null || emp.getEmployeeName().toLowerCase().contains(name.toLowerCase()))
                .filter(emp -> department == null || emp.getDepartment().equalsIgnoreCase(department))
                .filter(Employee::isActive)
                .toList();
        
        return employees.stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Conversion methods
    private EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO dto = EmployeeDTO.builder()
                .id(employee.getId())
                .employeeName(employee.getEmployeeName())
                .email(employee.getEmail())
                .gender(employee.getGender())
                .dateOfBirth(employee.getDateOfBirth())
                .joinDate(employee.getJoinDate())
                .mobileNumber(employee.getMobileNumber())
                .aadhaarNumber(employee.getAadhaarNumber())
                .accountNumber(employee.getAccountNumber())
                .department(employee.getDepartment())
                .designation(employee.getDesignation())
                .previousCompany(employee.getPreviousCompany())
                .pfNumber(employee.getPfNumber())
                .salary(employee.getSalary())
                // Map currentAddress to address field (primary address field)
                .address(employee.getCurrentAddress() != null ? employee.getCurrentAddress() : employee.getAddress())
                .permanentAddress(employee.getPermanrntAddress()) // Note: typo in entity
                .active(employee.isActive())
                .role(employee.getRole())
                .createdDate(employee.getCreatedDate())
                .updatedDate(employee.getUpdatedDate())
                .build();

        // Calculate computed fields
        calculateComputedFields(dto);
        return dto;
    }

    private Employee convertToEntity(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setEmployeeName(dto.getEmployeeName());
        employee.setEmail(dto.getEmail());
        employee.setGender(dto.getGender());
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setJoinDate(dto.getJoinDate());
        employee.setMobileNumber(dto.getMobileNumber());
        employee.setAadhaarNumber(dto.getAadhaarNumber());
        employee.setAccountNumber(dto.getAccountNumber());
        employee.setDepartment(dto.getDepartment());
        employee.setDesignation(dto.getDesignation());
        employee.setPreviousCompany(dto.getPreviousCompany());
        employee.setPfNumber(dto.getPfNumber());
        employee.setSalary(dto.getSalary());
        // Map address to currentAddress field (primary address field)
        employee.setCurrentAddress(dto.getAddress());
        employee.setAddress(dto.getAddress()); // Keep both for compatibility
        employee.setPermanrntAddress(dto.getPermanentAddress()); // Note: typo in entity
        employee.setActive(dto.getActive() != null ? dto.getActive() : true);
        employee.setRole(dto.getRole());
        return employee;
    }

    private void updateEntityFromDTO(Employee employee, EmployeeDTO dto) {
        if (dto.getEmployeeName() != null) employee.setEmployeeName(dto.getEmployeeName());
        if (dto.getEmail() != null) employee.setEmail(dto.getEmail());
        if (dto.getGender() != null) employee.setGender(dto.getGender());
        if (dto.getDateOfBirth() != null) employee.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getJoinDate() != null) employee.setJoinDate(dto.getJoinDate());
        if (dto.getMobileNumber() != null) employee.setMobileNumber(dto.getMobileNumber());
        if (dto.getAadhaarNumber() != null) employee.setAadhaarNumber(dto.getAadhaarNumber());
        if (dto.getAccountNumber() != null) employee.setAccountNumber(dto.getAccountNumber());
        if (dto.getDepartment() != null) employee.setDepartment(dto.getDepartment());
        if (dto.getDesignation() != null) employee.setDesignation(dto.getDesignation());
        if (dto.getPreviousCompany() != null) employee.setPreviousCompany(dto.getPreviousCompany());
        if (dto.getPfNumber() != null) employee.setPfNumber(dto.getPfNumber());
        if (dto.getSalary() != null) employee.setSalary(dto.getSalary());
        // Update address fields
        if (dto.getAddress() != null) {
            employee.setCurrentAddress(dto.getAddress());
            employee.setAddress(dto.getAddress()); // Keep both for compatibility
        }
        if (dto.getPermanentAddress() != null) employee.setPermanrntAddress(dto.getPermanentAddress()); // Note: typo in entity
        if (dto.getActive() != null) employee.setActive(dto.getActive());
        if (dto.getRole() != null) employee.setRole(dto.getRole());
    }

    private void calculateComputedFields(EmployeeDTO dto) {
        // Computed fields were removed from DTO to optimize data transfer
        // If needed, these calculations can be done on the frontend
        // or returned as separate fields in response maps
    }

    // Method to convert DTO to Map for controller compatibility
    public Map<String, Object> convertDtoToMap(EmployeeDTO dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", dto.getId());
        map.put("employeeName", dto.getEmployeeName());
        map.put("email", dto.getEmail());
        map.put("gender", dto.getGender());
        map.put("dateOfBirth", dto.getDateOfBirth());
        map.put("joinDate", dto.getJoinDate());
        map.put("mobileNumber", dto.getMobileNumber());
        map.put("aadhaarNumber", dto.getAadhaarNumber());
        map.put("accountNumber", dto.getAccountNumber());
        map.put("department", dto.getDepartment());
        map.put("designation", dto.getDesignation());
        map.put("previousCompany", dto.getPreviousCompany());
        map.put("pfNumber", dto.getPfNumber());
        map.put("salary", dto.getSalary());
        // Add address fields
        map.put("address", dto.getAddress());
        map.put("permanentAddress", dto.getPermanentAddress());
        map.put("active", dto.getActive());
        map.put("role", dto.getRole());
        map.put("createdDate", dto.getCreatedDate());
        map.put("updatedDate", dto.getUpdatedDate());
        
        // Add computed fields if needed
        if (dto.getGender() != null) {
            map.put("displayGender", dto.getGender().equals("M") ? "Male" : "Female");
        }
        if (dto.getSalary() != null) {
            map.put("formattedSalary", String.format("₹%.2f", dto.getSalary()));
        }
        if (dto.getDateOfBirth() != null) {
            try {
                int birthYear = Integer.parseInt(dto.getDateOfBirth().substring(0, 4));
                int age = LocalDate.now().getYear() - birthYear;
                map.put("age", age);
            } catch (Exception e) {
                map.put("age", null);
            }
        }
        if (dto.getJoinDate() != null) {
            try {
                int joinYear = Integer.parseInt(dto.getJoinDate().substring(0, 4));
                int yearsOfService = LocalDate.now().getYear() - joinYear;
                map.put("yearsOfService", yearsOfService);
                map.put("isNewEmployee", yearsOfService < 1);
            } catch (Exception e) {
                map.put("yearsOfService", null);
                map.put("isNewEmployee", false);
            }
        }
        
        return map;
    }
}
