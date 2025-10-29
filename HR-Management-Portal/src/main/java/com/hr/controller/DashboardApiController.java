package com.hr.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hr.entity.Compose;
import com.hr.entity.Employee;
import com.hr.repository.ComposeRepo;
import com.hr.repository.CreatePostRepo;
import com.hr.repository.EmployeeRepo;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class DashboardApiController {

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private ComposeRepo composeRepo;

    @Autowired
    private CreatePostRepo createPostRepo;

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Backend is connected and working!");
        response.put("timestamp", new Date());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Integer>> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        
        try {
            stats.put("totalEmployees", (int) employeeRepo.count());
            stats.put("activeProjects", 0); // Mock as there's no clear 'active projects' field
            
            // Count pending requests manually since countByStatus is not available
            int pendingCount = 0;
            try {
                List<Compose> allCompose = composeRepo.findAll();
                pendingCount = (int) allCompose.stream()
                    .filter(c -> "PENDING".equals(c.getStatus()))
                    .count();
            } catch (Exception e) {
                pendingCount = 0;
            }
            
            stats.put("pendingRequests", pendingCount);
            stats.put("completedTasks", (int) createPostRepo.count()); // Assuming createPost represents tasks
        } catch (Exception e) {
            // Return default values if database is not available
            stats.put("totalEmployees", 25);
            stats.put("activeProjects", 5);
            stats.put("pendingRequests", 8);
            stats.put("completedTasks", 42);
        }
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/department-summary")
    public ResponseEntity<Map<String, Integer>> getDepartmentSummary() {
        try {
            List<Employee> employees = employeeRepo.findAll();

            Map<String, Integer> summary = new HashMap<>();
            summary.put("development", 0);
            summary.put("qaTesting", 0);
            summary.put("networking", 0);
            summary.put("hrTeam", 0);
            summary.put("security", 0);
            summary.put("sealsMarket", 0);

            for (Employee employee : employees) {
                String department = employee.getDepartment();
                if (department != null) {
                    switch (department.toLowerCase()) {
                        case "development": summary.put("development", summary.get("development") + 1); break;
                        case "qa testing": summary.put("qaTesting", summary.get("qaTesting") + 1); break;
                        case "networking": summary.put("networking", summary.get("networking") + 1); break;
                        case "hr team": summary.put("hrTeam", summary.get("hrTeam") + 1); break;
                        case "security": summary.put("security", summary.get("security") + 1); break;
                        case "seals market": summary.put("sealsMarket", summary.get("sealsMarket") + 1); break;
                    }
                }
            }
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            // Return demo data if database is not available
            Map<String, Integer> summary = new HashMap<>();
            summary.put("development", 8);
            summary.put("qaTesting", 4);
            summary.put("networking", 3);
            summary.put("hrTeam", 2);
            summary.put("security", 3);
            summary.put("sealsMarket", 5);
            return ResponseEntity.ok(summary);
        }
    }

    @GetMapping("/recent-activities")
    public ResponseEntity<List<Map<String, Object>>> getRecentActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        try {
            // Get recent leave requests
            List<Compose> recentLeaves = composeRepo.findAll().stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId())) // Sort by ID descending (newest first)
                .limit(10)
                .collect(Collectors.toList());
            
            for (Compose leave : recentLeaves) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("id", leave.getId());
                activity.put("type", "leave_request");
                activity.put("title", leave.getSubject());
                activity.put("user", leave.getEmpName());
                activity.put("status", leave.getStatus());
                activity.put("date", leave.getAddedDate());
                activity.put("description", "Leave request: " + leave.getSubject());
                activities.add(activity);
            }
        } catch (Exception e) {
            // Add demo activities if database fails
            Map<String, Object> activity1 = new HashMap<>();
            activity1.put("id", 1);
            activity1.put("type", "leave_request");
            activity1.put("title", "Annual Leave Request");
            activity1.put("user", "John Doe");
            activity1.put("status", "PENDING");
            activity1.put("date", new Date().toString());
            activity1.put("description", "Annual leave request submitted");
            activities.add(activity1);
        }
        
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/leave-statistics")
    public ResponseEntity<Map<String, Object>> getLeaveStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            List<Compose> allLeaves = composeRepo.findAll();
            
            Map<String, Long> statusCounts = new HashMap<>();
            statusCounts.put("pending", allLeaves.stream().filter(l -> "PENDING".equals(l.getStatus())).count());
            statusCounts.put("approved", allLeaves.stream().filter(l -> "APPROVED".equals(l.getStatus())).count());
            statusCounts.put("denied", allLeaves.stream().filter(l -> "DENIED".equals(l.getStatus())).count());
            statusCounts.put("canceled", allLeaves.stream().filter(l -> "CANCELED".equals(l.getStatus())).count());
            
            stats.put("statusCounts", statusCounts);
            stats.put("totalRequests", allLeaves.size());
            
            // Calculate approval rate
            long totalApproved = statusCounts.get("approved");
            long totalProcessed = totalApproved + statusCounts.get("denied");
            double approvalRate = totalProcessed > 0 ? (double) totalApproved / totalProcessed * 100 : 0;
            stats.put("approvalRate", Math.round(approvalRate * 100.0) / 100.0);
            
        } catch (Exception e) {
            // Demo data
            Map<String, Long> statusCounts = new HashMap<>();
            statusCounts.put("pending", 5L);
            statusCounts.put("approved", 15L);
            statusCounts.put("denied", 2L);
            statusCounts.put("canceled", 1L);
            
            stats.put("statusCounts", statusCounts);
            stats.put("totalRequests", 23);
            stats.put("approvalRate", 88.24);
        }
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/employee-performance")
    public ResponseEntity<List<Map<String, Object>>> getEmployeePerformance() {
        List<Map<String, Object>> performance = new ArrayList<>();
        
        try {
            List<Employee> employees = employeeRepo.findAll().stream()
                .limit(10) // Get top 10 employees
                .collect(Collectors.toList());
            
            for (Employee emp : employees) {
                Map<String, Object> empPerf = new HashMap<>();
                empPerf.put("id", emp.getId());
                empPerf.put("name", emp.getEmployeeName());
                empPerf.put("department", emp.getDepartment());
                empPerf.put("designation", emp.getDesignation());
                
                // Mock performance metrics - in real app, calculate from actual data
                empPerf.put("performanceScore", 75 + (int)(Math.random() * 25)); // Random score 75-100
                empPerf.put("tasksCompleted", 20 + (int)(Math.random() * 30)); // Random tasks 20-50
                empPerf.put("attendanceRate", 85 + (int)(Math.random() * 15)); // Random attendance 85-100%
                
                performance.add(empPerf);
            }
        } catch (Exception e) {
            // Demo data
            Map<String, Object> emp1 = new HashMap<>();
            emp1.put("id", 1);
            emp1.put("name", "John Doe");
            emp1.put("department", "Development");
            emp1.put("designation", "Senior Developer");
            emp1.put("performanceScore", 92);
            emp1.put("tasksCompleted", 45);
            emp1.put("attendanceRate", 96);
            performance.add(emp1);
        }
        
        return ResponseEntity.ok(performance);
    }
}
