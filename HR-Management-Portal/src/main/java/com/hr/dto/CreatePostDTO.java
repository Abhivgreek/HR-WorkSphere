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
 * Optimized DTO for CreatePost entity
 * Removed unused fields: category, priority, status, statistics, computed fields
 * Used for data transfer between frontend and backend
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatePostDTO {

    private Integer id;

    @NotNull(message = "Title is required")
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 5, max = 200, message = "Title must be between 5 to 200 characters")
    private String title;

    @NotNull(message = "Content is required")
    @NotBlank(message = "Content cannot be blank")
    @Size(min = 10, max = 5000, message = "Content must be between 10 to 5000 characters")
    private String content;

    private Integer authorId;
    private String authorName;
    private String authorEmail;
    private String authorRole;
    private String department;

    @Builder.Default
    private Boolean isActive = true;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDate;

    // Constructor with essential fields
    public CreatePostDTO(String title, String content, Integer authorId) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.isActive = true;
    }
}
