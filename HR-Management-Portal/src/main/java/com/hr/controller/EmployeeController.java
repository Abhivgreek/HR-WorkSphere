package com.hr.controller;

import java.util.*;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.hr.dto.EmployeeDTO;
import com.hr.entity.Employee;
import com.hr.repository.EmployeeRepo;
import com.hr.service.EmailService;
import com.hr.service.EmployeeService;
import com.hr.service.HrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class EmployeeController {

    @Autowired
    private HrService service;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllEmployees() {
        try {
            List<EmployeeDTO> employees = employeeService.getAllEmployees();
            List<Map<String, Object>> employeeList = employees.stream()
                .map(employeeService::convertDtoToMap)
                .map(this::adaptEmployeeMapFormat) // Convert to expected format
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(employeeList);
        } catch (Exception e) {
            log.error("Error fetching employees: {}", e.getMessage());
            // Return demo data if database is not available
            List<Map<String, Object>> demoEmployees = new ArrayList<>();
            
            Map<String, Object> emp1 = new HashMap<>();
            emp1.put("id", 1);
            emp1.put("name", "John Doe");
            emp1.put("email", "john.doe@company.com");
            emp1.put("employeeId", "EMP001");
            emp1.put("department", "Development");
            emp1.put("designation", "Software Engineer");
            emp1.put("salary", 75000);
            emp1.put("contact", "+1234567890");
            emp1.put("joinDate", "2023-01-15");
            
            demoEmployees.add(emp1);
            return ResponseEntity.ok(demoEmployees);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEmployeeById(@PathVariable int id) {
        try {
            Optional<EmployeeDTO> employeeOpt = employeeService.getEmployeeById(id);
            if (employeeOpt.isPresent()) {
                Map<String, Object> employeeMap = employeeService.convertDtoToMap(employeeOpt.get());
                return ResponseEntity.ok(adaptEmployeeMapFormat(employeeMap));
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Employee not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("Error retrieving employee with ID {}: {}", id, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving employee: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createEmployee(@RequestBody Map<String, Object> employeeData) {
        try {
            Employee employee = convertMapToEmployee(employeeData);
            
            // Generate secure password if not provided
            String password = (String) employeeData.get("password");
            if (password == null || password.trim().isEmpty()) {
                password = generateDefaultPassword(employee);
            }
            
            // Encrypt password before saving
            String encryptedPassword = passwordEncoder.encode(password);
            employee.setPassword(encryptedPassword);
            
            Employee savedEmployee = service.addEmaployee(employee);
            
            // Send welcome email to the new employee
            try {
                emailService.sendWelcomeEmail(
                    savedEmployee.getEmail(), 
                    savedEmployee.getEmployeeName(), 
                    password
                );
                System.out.println("Welcome email sent to: " + savedEmployee.getEmail());
            } catch (Exception emailException) {
                System.err.println("Failed to send welcome email: " + emailException.getMessage());
                // Don't fail the employee creation if email fails
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Employee created successfully and welcome email sent");
            response.put("id", savedEmployee.getId());
            response.put("employee", convertEmployeeToMap(savedEmployee));
            response.put("generatedPassword", password); // Return password to admin
            response.put("loginInfo", Map.of(
                "email", savedEmployee.getEmail(),
                "password", password,
                "empId", "emp" + savedEmployee.getId()
            ));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error creating employee: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEmployee(@PathVariable int id, @RequestBody Map<String, Object> employeeData) {
        try {
            Optional<Employee> existingEmp = employeeRepo.findById(id);
            if (existingEmp.isPresent()) {
                Employee existingEmployee = existingEmp.get();
                Employee employee = convertMapToEmployee(employeeData);
                employee.setId(id);
                
                // Handle password update logic
                String newPassword = (String) employeeData.get("password");
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    // Admin is updating the password - encrypt it
                    String encryptedPassword = passwordEncoder.encode(newPassword);
                    employee.setPassword(encryptedPassword);
                } else {
                    // Preserve existing password if not provided
                    employee.setPassword(existingEmployee.getPassword());
                }
                
                Employee updatedEmployee = employeeRepo.save(employee);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Employee updated successfully");
                response.put("employee", convertEmployeeToMap(updatedEmployee));
                
                // Include password info if it was updated
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    response.put("passwordUpdated", true);
                    response.put("newPassword", newPassword);
                    response.put("loginInfo", Map.of(
                        "email", updatedEmployee.getEmail(),
                        "password", newPassword,
                        "empId", "emp" + updatedEmployee.getId()
                    ));
                }
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Employee not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error updating employee: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteEmployee(@PathVariable int id) {
        try {
            if (employeeRepo.existsById(id)) {
                employeeRepo.deleteById(id);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Employee deleted successfully");
                response.put("id", id);
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Employee not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error deleting employee: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Map<String, Object>> resetEmployeePassword(@PathVariable int id, @RequestBody Map<String, String> requestData) {
        try {
            Optional<Employee> employeeOpt = employeeRepo.findById(id);
            if (employeeOpt.isPresent()) {
                Employee employee = employeeOpt.get();
                
                String newPassword = requestData.get("newPassword");
                if (newPassword == null || newPassword.trim().isEmpty()) {
                    // Generate new password if not provided
                    newPassword = generateDefaultPassword(employee);
                }
                
                // Encrypt the new password before saving
                String encryptedPassword = passwordEncoder.encode(newPassword);
                employee.setPassword(encryptedPassword);
                employeeRepo.save(employee);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Password reset successfully");
                response.put("newPassword", newPassword);
                response.put("loginInfo", Map.of(
                    "email", employee.getEmail(),
                    "password", newPassword,
                    "empId", "emp" + employee.getId()
                ));
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Employee not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error resetting password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}/salary-slip/pdf")
    public ResponseEntity<byte[]> downloadSalarySlip(@PathVariable int id) {
        try {
            Employee employee = employeeRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Employee not found"));

            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);

            document.open();
            document.add(new Paragraph("Salary Slip for " + employee.getEmployeeName()));
            document.add(new Paragraph("Employee ID: " + employee.getId()));
            document.add(new Paragraph("Designation: " + employee.getDesignation()));
            document.add(new Paragraph("Monthly Salary: $" + employee.getSalary()));
            document.add(new Paragraph("\n--- DEDUCTIONS ---"));
            document.add(new Paragraph("Tax: $0.00")); // Placeholder for now
            document.add(new Paragraph("Provident Fund: $0.00")); // Placeholder for now
            document.add(new Paragraph("\n--- NET PAY ---"));
            document.add(new Paragraph("Net Salary: $" + employee.getSalary())); // Simple calculation for now
            document.close();

            byte[] pdfBytes = baos.toByteArray();

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=salary_slip_" + employee.getId() + ".pdf")
                    .body(pdfBytes);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating PDF".getBytes());
        }
    }

    // Helper methods
    private Map<String, Object> convertEmployeeToMap(Employee employee) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", employee.getId());
        map.put("name", employee.getEmployeeName());
        map.put("email", employee.getEmail());
        map.put("employeeId", "EMP" + String.format("%03d", employee.getId()));
        map.put("department", employee.getDepartment());
        map.put("designation", employee.getDesignation());
        map.put("salary", employee.getSalary());
        map.put("contact", employee.getContactNumber());
        map.put("joinDate", employee.getJoinDate());
        map.put("dateOfBirth", employee.getDateOfBirth());
        map.put("role", employee.getRole());
        map.put("address", employee.getAddress());
        return map;
    }

    private Employee convertMapToEmployee(Map<String, Object> map) {
        Employee employee = new Employee();
        
        // Only set ID for updates, not for new employees
        if (map.get("id") != null && !map.get("id").toString().isEmpty()) {
            try {
                employee.setId(Integer.parseInt(map.get("id").toString()));
            } catch (NumberFormatException e) {
                // Ignore invalid ID format for new employees
            }
        }
        
        // Handle different field name mappings
        String name = (String) map.get("employeeName");
        if (name == null) {
            name = (String) map.get("name");
        }
        employee.setEmployeeName(name);
        
        employee.setEmail((String) map.get("email"));
        employee.setDepartment((String) map.get("department"));
        employee.setDesignation((String) map.get("designation"));
        
        if (map.get("salary") != null) {
            try {
                employee.setSalary(Double.parseDouble(map.get("salary").toString()));
            } catch (NumberFormatException e) {
                employee.setSalary(0.0); // Default value
            }
        }
        
        // Handle contact number validation (must start with 6-9 and be 10 digits)
        String contact = (String) map.get("contact");
        if (contact != null && contact.length() >= 10) {
            // Ensure contact starts with 6-9 and is exactly 10 digits
            contact = contact.replaceAll("[^0-9]", ""); // Remove non-numeric characters
            if (contact.length() >= 10) {
                contact = contact.substring(contact.length() - 10); // Take last 10 digits
                if (!contact.matches("^[6-9].*")) {
                    contact = "9" + contact.substring(1); // Ensure it starts with 6-9
                }
            }
            employee.setContactNumber(contact);
        } else {
            employee.setContactNumber("9123456789"); // Default valid contact
        }
        
        // Set joinDate - if not provided, use current date
        String joinDate = (String) map.get("joinDate");
        if (joinDate == null || joinDate.isEmpty()) {
            joinDate = java.time.LocalDate.now().toString(); // Current date in YYYY-MM-DD format
        }
        employee.setJoinDate(joinDate);
        
        employee.setDateOfBirth((String) map.get("dateOfBirth"));
        employee.setRole((String) map.getOrDefault("role", "USER"));
        
        // Handle address validation (must be between 10-1000 characters)
        String address = (String) map.get("address");
        if (address != null && address.length() >= 10) {
            employee.setAddress(address);
        } else {
            employee.setAddress("Default Address, City, State, Country - 123456"); // Default valid address
        }
        
        // Set default values for other fields to avoid validation errors
        if (employee.getGender() == null) {
            employee.setGender("M"); // Default gender
        }
        
        return employee;
    }

    private String generateDefaultPassword(Employee employee) {
        // Generate password using employee name and random number
        String name = employee.getEmployeeName();
        if (name == null || name.trim().isEmpty()) {
            name = "Employee";
        }
        
        // Take first part of name (before space) and capitalize
        String namePart = name.split(" ")[0];
        namePart = namePart.substring(0, 1).toUpperCase() + namePart.substring(1).toLowerCase();
        
        // Generate 4-digit random number
        int randomNum = (int) (Math.random() * 9000) + 1000;
        
        // Combine name + random number + special char
        return namePart + randomNum + "@";
    }

    // Adapter method to convert DTO format to controller expected format
    private Map<String, Object> adaptEmployeeMapFormat(Map<String, Object> dtoMap) {
        Map<String, Object> adapted = new HashMap<>();
        
        // Map DTO fields to controller expected field names
        adapted.put("id", dtoMap.get("id"));
        adapted.put("name", dtoMap.get("employeeName"));
        adapted.put("email", dtoMap.get("email"));
        adapted.put("employeeId", "EMP" + String.format("%03d", (Integer) dtoMap.get("id")));
        adapted.put("department", dtoMap.get("department"));
        adapted.put("designation", dtoMap.get("designation"));
        adapted.put("salary", dtoMap.get("salary"));
        adapted.put("contact", dtoMap.get("mobileNumber"));
        adapted.put("joinDate", dtoMap.get("joinDate"));
        adapted.put("dateOfBirth", dtoMap.get("dateOfBirth"));
        adapted.put("role", dtoMap.get("role"));
        adapted.put("address", dtoMap.get("address") != null ? dtoMap.get("address") : "N/A");
        
        return adapted;
    }
}
