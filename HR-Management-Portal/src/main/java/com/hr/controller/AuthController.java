package com.hr.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.hr.entity.Employee;
import com.hr.repository.EmployeeRepo;
import com.hr.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class AuthController {

    @Autowired
    private EmployeeRepo employeeRepo;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        Map<String, Object> response = new HashMap<>();
        Employee employee = null;
        
        try {
            // First, try to authenticate using email and password
            if (username.contains("@")) {
                // Email-based authentication - find by email first, then verify password
                Optional<Employee> empOpt = employeeRepo.findAll().stream()
                    .filter(emp -> emp.getEmail().equals(username))
                    .findFirst();
                
                if (empOpt.isPresent()) {
                    Employee emp = empOpt.get();
                    // Check if password matches (either bcrypt or plain text for backward compatibility)
                    if ((emp.getPassword() != null && 
                         (passwordEncoder.matches(password, emp.getPassword()) || 
                          password.equals(emp.getPassword())))) {
                        employee = emp;
                    }
                }
            } else if (username.startsWith("emp")) {
                // Employee ID-based authentication (format like "emp123")
                String empId = username.substring(3);
                int employeeId = Integer.parseInt(empId);
                Optional<Employee> empOpt = employeeRepo.findById(employeeId);
                
                if (empOpt.isPresent()) {
                    Employee emp = empOpt.get();
                    // Check if password matches (either bcrypt or plain text for backward compatibility)
                    if ((emp.getPassword() != null && 
                         (passwordEncoder.matches(password, emp.getPassword()) || 
                          password.equals(emp.getPassword())))) {
                        employee = emp;
                    }
                }
            }
            
            if (employee != null && employee.isActive()) {
                // Generate JWT token
                String token = jwtUtil.generateToken(
                    employee.getId(), 
                    username, 
                    employee.getRole(), 
                    employee.getEmail(), 
                    employee.getEmployeeName()
                );
                String refreshToken = jwtUtil.generateRefreshToken(username);
                
                Map<String, Object> user = new HashMap<>();
                user.put("id", employee.getId());
                user.put("username", username);
                user.put("role", employee.getRole());
                user.put("name", employee.getEmployeeName());
                user.put("designation", employee.getDesignation());
                user.put("email", employee.getEmail());
                
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("user", user);
                response.put("token", token);
                response.put("refreshToken", refreshToken);
                response.put("expiresIn", 86400); // 24 hours in seconds
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            // Fallback for demo authentication
            if (("admin".equals(username) && "admin".equals(password)) || 
                ("user".equals(username) && "user".equals(password))) {
                
                String role = username.equals("admin") ? "ADMIN" : "USER";
                String name = username.equals("admin") ? "Administrator" : "User";
                String email = username.equals("admin") ? "admin@hr.com" : "user@hr.com";
                Integer userId = username.equals("admin") ? 1 : 2;
                
                // Generate JWT token for demo user
                String token = jwtUtil.generateToken(userId, username, role, email, name);
                String refreshToken = jwtUtil.generateRefreshToken(username);
                
                Map<String, Object> user = new HashMap<>();
                user.put("id", userId);
                user.put("username", username);
                user.put("role", role);
                user.put("name", name);
                user.put("designation", username.equals("admin") ? "HR Manager" : "Employee");
                user.put("email", email);
                
                response.put("success", true);
                response.put("message", "Login successful (demo mode)");
                response.put("user", user);
                response.put("token", token);
                response.put("refreshToken", refreshToken);
                response.put("expiresIn", 86400);
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> passwordData) {
        System.out.println("Received password change request: " + passwordData);
        try {
            String userIdStr = passwordData.get("userId");
            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");
            
            System.out.println("UserID: " + userIdStr + ", CurrentPassword provided: " + (currentPassword != null) + ", NewPassword provided: " + (newPassword != null));
            
            if (userIdStr == null || currentPassword == null || newPassword == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Missing required fields");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            int userId = Integer.parseInt(userIdStr);
            
            // Check if this is a demo user (userId 1 is typically demo)
            if (userId == 1) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Cannot change password for demo users. Please create a real employee account first.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            Optional<Employee> employeeOpt = employeeRepo.findById(userId);
            
            if (employeeOpt.isPresent()) {
                Employee employee = employeeOpt.get();
                
                System.out.println("Found employee: " + employee.getEmployeeName() + ", stored password: " + employee.getPassword());
                
                // Verify current password (support both bcrypt and plain text)
                boolean passwordMatches = false;
                if (employee.getPassword() != null) {
                    passwordMatches = passwordEncoder.matches(currentPassword, employee.getPassword()) ||
                                    currentPassword.equals(employee.getPassword());
                }
                
                if (!passwordMatches) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Current password is incorrect");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
                
                // Update password with bcrypt encoding
                String encodedPassword = passwordEncoder.encode(newPassword);
                employee.setPassword(encodedPassword);
                employeeRepo.save(employee);
                
                System.out.println("Password updated successfully for user: " + employee.getEmployeeName());
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Password changed successfully");
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Employee record not found in database. Please contact admin to create your employee profile first.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error changing password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> tokenData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String refreshToken = tokenData.get("refreshToken");
            
            if (refreshToken == null) {
                response.put("success", false);
                response.put("message", "Refresh token is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            if (jwtUtil.isTokenValid(refreshToken)) {
                String username = jwtUtil.getUsernameFromToken(refreshToken);
                
                // Find user by username (could be email or employee ID)
                Employee employee = null;
                if (username.contains("@")) {
                    employee = employeeRepo.findByEmailAndPassword(username, null);
                } else {
                    try {
                        if (username.startsWith("emp")) {
                            String empId = username.substring(3);
                            int employeeId = Integer.parseInt(empId);
                            Optional<Employee> empOpt = employeeRepo.findById(employeeId);
                            if (empOpt.isPresent()) {
                                employee = empOpt.get();
                            }
                        } else {
                            // Handle demo users
                            if ("admin".equals(username) || "user".equals(username)) {
                                String role = username.equals("admin") ? "ADMIN" : "USER";
                                String name = username.equals("admin") ? "Administrator" : "User";
                                String email = username.equals("admin") ? "admin@hr.com" : "user@hr.com";
                                Integer userId = username.equals("admin") ? 1 : 2;
                                
                                String newToken = jwtUtil.generateToken(userId, username, role, email, name);
                                String newRefreshToken = jwtUtil.generateRefreshToken(username);
                                
                                response.put("success", true);
                                response.put("token", newToken);
                                response.put("refreshToken", newRefreshToken);
                                response.put("expiresIn", 86400);
                                
                                return ResponseEntity.ok(response);
                            }
                        }
                    } catch (Exception e) {
                        // Handle parsing errors
                    }
                }
                
                if (employee != null && employee.isActive()) {
                    String newToken = jwtUtil.generateToken(
                        employee.getId(), 
                        username, 
                        employee.getRole(), 
                        employee.getEmail(), 
                        employee.getEmployeeName()
                    );
                    String newRefreshToken = jwtUtil.generateRefreshToken(username);
                    
                    response.put("success", true);
                    response.put("token", newToken);
                    response.put("refreshToken", newRefreshToken);
                    response.put("expiresIn", 86400);
                    
                    return ResponseEntity.ok(response);
                } else {
                    response.put("success", false);
                    response.put("message", "User not found or inactive");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            } else {
                response.put("success", false);
                response.put("message", "Invalid or expired refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error refreshing token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody Map<String, String> tokenData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String token = tokenData.get("token");
            
            if (token == null) {
                response.put("valid", false);
                response.put("message", "Token is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            if (jwtUtil.isTokenValid(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                Integer userId = jwtUtil.getUserIdFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);
                String email = jwtUtil.getEmailFromToken(token);
                String name = jwtUtil.getNameFromToken(token);
                long remainingTime = jwtUtil.getTokenExpirationTime(token);
                
                Map<String, Object> user = new HashMap<>();
                user.put("id", userId);
                user.put("username", username);
                user.put("role", role);
                user.put("email", email);
                user.put("name", name);
                
                response.put("valid", true);
                response.put("user", user);
                response.put("remainingTime", remainingTime);
                
                return ResponseEntity.ok(response);
            } else {
                response.put("valid", false);
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("valid", false);
            response.put("message", "Error validating token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        // In a production system, you might want to blacklist the token
        // For now, we'll just return a success response
        response.put("success", true);
        response.put("message", "Logged out successfully");
        
        return ResponseEntity.ok(response);
    }
}
