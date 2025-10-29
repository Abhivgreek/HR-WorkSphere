package com.hr.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr.entity.Compose;
import com.hr.repository.ComposeRepo;
import com.hr.repository.EmployeeRepo;
import com.hr.service.LeaveService;

@RestController
@RequestMapping("/api/leave")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class LeaveController {

    @Autowired
    private ComposeRepo composeRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private LeaveService leaveService;

    @GetMapping("/requests")
    public ResponseEntity<List<Map<String, Object>>> getAllLeaveRequests() {
        try {
            List<Compose> composeItems = composeRepo.findAll();
            List<Map<String, Object>> composeList = composeItems.stream()
                .map(this::convertComposeToMap)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(composeList);
        } catch (Exception e) {
            List<Map<String, Object>> demoCompose = new ArrayList<>();
            
            Map<String, Object> item1 = new HashMap<>();
            item1.put("id", 1);
            item1.put("subject", "Sample Leave Request");
            item1.put("text", "This is a sample leave request.");
            item1.put("status", "PENDING");
            item1.put("parentUkid", 1);
            item1.put("empName", "John Doe");
            item1.put("addedDate", new Date().toString());
            
            demoCompose.add(item1);
            return ResponseEntity.ok(demoCompose);
        }
    }

    @GetMapping("/requests/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getLeaveRequestsByUser(@PathVariable int userId) {
        try {
            List<Compose> composeItems = composeRepo.findByParentUkid(userId);
            List<Map<String, Object>> composeList = composeItems.stream()
                .map(this::convertComposeToMap)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(composeList);
        } catch (Exception e) {
            List<Map<String, Object>> demoCompose = new ArrayList<>();
            return ResponseEntity.ok(demoCompose);
        }
    }

    @PostMapping("/requests")
    public ResponseEntity<Map<String, Object>> createLeaveRequest(@RequestBody Map<String, Object> leaveData) {
        try {
            // Debug logging
            System.out.println("Received leave request data: " + leaveData);
            
            Compose compose = new Compose();
            compose.setSubject((String) leaveData.get("subject"));
            compose.setStatus("PENDING");
            compose.setEmpName((String) leaveData.get("empName"));
            compose.setPosition((String) leaveData.get("position"));
            compose.setAddedDate(new Date().toString());
            
            // Set parent user ID
            if (leaveData.get("parentUkid") != null) {
                compose.setParentUkid(Integer.parseInt(leaveData.get("parentUkid").toString()));
            }
            
            // Build structured text with leave details
            StringBuilder textBuilder = new StringBuilder();
            
            String leaveType = (String) leaveData.get("leaveType");
            String fromDate = (String) leaveData.get("fromDate");
            String toDate = (String) leaveData.get("toDate");
            Object leaveDaysObj = leaveData.get("leaveDays");
            String reason = (String) leaveData.get("reason");
            
            textBuilder.append("Leave Type: ").append(leaveType != null ? leaveType : "N/A").append("\n");
            textBuilder.append("From: ").append(fromDate != null ? fromDate : "N/A").append("\n");
            textBuilder.append("To: ").append(toDate != null ? toDate : "N/A").append("\n");
            textBuilder.append("Days: ").append(leaveDaysObj != null ? leaveDaysObj.toString() : "N/A").append("\n");
            textBuilder.append("Reason: ").append(reason != null ? reason : "N/A");
            
            compose.setText(textBuilder.toString());
            
            System.out.println("Final leave request text: " + compose.getText());
            
            Compose savedCompose = composeRepo.save(compose);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Leave request submitted successfully");
            response.put("id", savedCompose.getId());
            response.put("leaveRequest", convertComposeToMap(savedCompose));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error creating leave request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/requests/{id}/status")
    public ResponseEntity<Map<String, Object>> updateLeaveStatus(@PathVariable int id, @RequestBody Map<String, String> statusUpdate) {
        try {
            Optional<Compose> composeOpt = composeRepo.findById(id);
            if (composeOpt.isPresent()) {
                Compose compose = composeOpt.get();
                String newStatus = statusUpdate.get("status");
                compose.setStatus(newStatus);
                
                // If approved, deduct leaves from user's balance
                if ("APPROVED".equals(newStatus)) {
                    try {
                        Integer leaveDays = extractLeaveDaysFromText(compose.getText());
                        if (leaveDays != null && leaveDays > 0) {
                            leaveService.deductLeaves(compose.getParentUkid(), leaveDays);
                            System.out.println("Deducted " + leaveDays + " leave days from employee " + compose.getParentUkid());
                        }
                    } catch (RuntimeException e) {
                        System.err.println("Error deducting leaves: " + e.getMessage());
                        // Continue with status update even if leave deduction fails
                    }
                }
                
                composeRepo.save(compose);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Leave request status updated successfully");
                response.put("id", id);
                response.put("status", newStatus);
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Leave request not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error updating leave request status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/summary/{employeeId}")
    public ResponseEntity<Map<String, Integer>> getLeaveSummary(@PathVariable int employeeId) {
        try {
            List<Compose> leaves = composeRepo.findByParentUkid(employeeId);

            int pending = 0, approved = 0, canceled = 0, denied = 0, total = 0, used = 0;
            for (Compose leave : leaves) {
                String status = leave.getStatus() != null ? leave.getStatus().toUpperCase() : "";
                Integer days = extractLeaveDaysFromText(leave.getText());
                if (days == null) days = 0;
                
                switch (status) {
                    case "PENDING": pending += 1; break;
                    case "APPROVED": approved += 1; used += days; break;
                    case "CANCELED": canceled += 1; used += days; break;
                    case "DENIED": denied += 1; used += days; break;
                }
                total += 1;
            }
            
            int remaining = Math.max(40 - used, 0); // Assuming 40 days annual leave
            
            Map<String, Integer> summary = new HashMap<>();
            summary.put("pending", pending);
            summary.put("approved", approved);
            summary.put("canceled", canceled);
            summary.put("denied", denied);
            summary.put("total", total);
            summary.put("remaining", remaining);
            summary.put("used", used);
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            Map<String, Integer> emptySummary = new HashMap<>();
            emptySummary.put("pending", 0);
            emptySummary.put("approved", 0);
            emptySummary.put("canceled", 0);
            emptySummary.put("denied", 0);
            emptySummary.put("total", 0);
            emptySummary.put("remaining", 40);
            emptySummary.put("used", 0);
            return ResponseEntity.ok(emptySummary);
        }
    }

    @GetMapping("/balance/{employeeId}")
    public ResponseEntity<Map<String, Object>> getLeaveBalance(@PathVariable int employeeId) {
        try {
            Map<String, Object> balance = new HashMap<>();
            
            // Get leave balance from service if available
            try {
                var leaveTracker = leaveService.getLeaveBalance(employeeId);
                balance.put("availableLeaves", leaveTracker.getAvailableLeaves());
                balance.put("usedLeaves", leaveTracker.getUsedLeaves());
                balance.put("totalLeaves", leaveTracker.getTotalLeaves());
            } catch (Exception e) {
                // Fallback to manual calculation
                List<Compose> leaves = composeRepo.findByParentUkid(employeeId);
                int usedLeaves = leaves.stream()
                    .filter(leave -> "APPROVED".equals(leave.getStatus()))
                    .mapToInt(leave -> {
                        Integer days = extractLeaveDaysFromText(leave.getText());
                        return days != null ? days : 0;
                    })
                    .sum();
                
                balance.put("availableLeaves", Math.max(40 - usedLeaves, 0));
                balance.put("usedLeaves", usedLeaves);
                balance.put("totalLeaves", 40);
            }
            
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            Map<String, Object> defaultBalance = new HashMap<>();
            defaultBalance.put("availableLeaves", 40);
            defaultBalance.put("usedLeaves", 0);
            defaultBalance.put("totalLeaves", 40);
            return ResponseEntity.ok(defaultBalance);
        }
    }

    // Helper methods
    private Map<String, Object> convertComposeToMap(Compose compose) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", compose.getId());
        map.put("subject", compose.getSubject());
        map.put("text", compose.getText());
        map.put("status", compose.getStatus());
        map.put("parentUkid", compose.getParentUkid());
        map.put("empName", compose.getEmpName());
        map.put("addedDate", compose.getAddedDate());
        map.put("position", compose.getPosition());
        
        // Extract structured leave information from text
        if (compose.getText() != null) {
            Map<String, String> leaveDetails = parseLeaveDetailsFromText(compose.getText());
            map.putAll(leaveDetails);
        }
        
        return map;
    }

    private Map<String, String> parseLeaveDetailsFromText(String text) {
        Map<String, String> details = new HashMap<>();
        if (text == null) return details;
        
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim().toLowerCase().replaceAll(" ", "");
                    String value = parts[1].trim();
                    details.put(key, value);
                }
            }
        }
        return details;
    }

    private Integer extractLeaveDaysFromText(String text) {
        if (text == null) return null;
        
        try {
            String[] lines = text.split("\n");
            for (String line : lines) {
                if (line.contains("Days:")) {
                    String[] parts = line.split("Days:");
                    if (parts.length > 1) {
                        String daysStr = parts[1].trim().split("\\s+")[0]; // Get first word after "Days:"
                        return Integer.parseInt(daysStr);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing leave days from text: " + e.getMessage());
        }
        return null;
    }
}
