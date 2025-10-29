package com.hr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Optimized DTO for Compose entity
 * Removed threading, priority, attachment features not used by frontend
 * Used for data transfer between frontend and backend for messaging
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComposeDTO {

    private Integer id;

    @NotNull(message = "Sender ID is required")
    private Integer senderId;
    
    private String senderName;
    private String senderEmail;
    private String senderRole;
    private String senderDepartment;

    @NotNull(message = "Recipient ID is required")
    private Integer recipientId;
    
    private String recipientName;
    private String recipientEmail;
    private String recipientRole;
    private String recipientDepartment;

    @NotNull(message = "Subject is required")
    @NotBlank(message = "Subject cannot be blank")
    @Size(min = 3, max = 200, message = "Subject must be between 3 to 200 characters")
    private String subject;

    @NotNull(message = "Message content is required")
    @NotBlank(message = "Message content cannot be blank")
    @Size(min = 10, max = 5000, message = "Message content must be between 10 to 5000 characters")
    private String message;

    @Size(max = 20, message = "Status cannot exceed 20 characters")
    @Builder.Default
    private String status = "SENT"; // DRAFT, SENT, READ, REPLIED

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDate;

    // Constructor with essential fields
    public ComposeDTO(Integer senderId, Integer recipientId, String subject, String message) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.subject = subject;
        this.message = message;
        this.status = "SENT";
        this.sentDate = LocalDateTime.now();
    }


    // Essential utility methods for status checks
    public boolean isSent() {
        return "SENT".equalsIgnoreCase(status);
    }

    public boolean isDraft() {
        return "DRAFT".equalsIgnoreCase(status);
    }

    public boolean isRead() {
        return "READ".equalsIgnoreCase(status);
    }
}
