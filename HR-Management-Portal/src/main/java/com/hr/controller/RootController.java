package com.hr.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class RootController {

    @Autowired
    private AuthController authController;
    
    @Autowired
    private DashboardApiController dashboardController;

    // Root-level login endpoint for frontend compatibility
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> rootLogin(@RequestBody Map<String, String> credentials) {
        // Delegate to the AuthController login method
        return authController.login(credentials);
    }
    
    // Root-level test endpoint for backend connectivity
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> rootTest() {
        // Delegate to the DashboardController test method
        return dashboardController.testConnection();
    }
}
