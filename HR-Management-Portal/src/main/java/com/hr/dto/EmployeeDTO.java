package com.hr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * DTO for Employee entity
 * Used for data transfer between frontend and backend
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDTO {

    private Integer id;

    @NotBlank(message = "Employee name is required")
    @Size(min = 3, max = 100, message = "Employee name must be between 3 to 100 characters")
    private String employeeName;

    @Email(message = "Please provide a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "^(M|F)$", message = "Gender must be either M (Male) or F (Female)")
    private String gender;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date of birth must be in YYYY-MM-DD format")
    @NotBlank(message = "Date of birth is required")
    private String dateOfBirth;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Join date must be in YYYY-MM-DD format")
    @NotBlank(message = "Join date is required")
    private String joinDate;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number must start with 6-9 and be 10 digits long")
    private String mobileNumber;

    @Pattern(regexp = "^\\d{12}$", message = "Aadhaar number must be exactly 12 digits")
    private String aadhaarNumber;

    @Pattern(regexp = "^\\d{9,18}$", message = "Account number must be between 9 to 18 digits")
    private String accountNumber;

    @NotBlank(message = "Department is required")
    @Size(min = 2, max = 100, message = "Department must be between 2 to 100 characters")
    private String department;

    @Size(min = 2, max = 100, message = "Designation must be between 2 to 100 characters")
    private String designation;

    @Size(min = 2, max = 100, message = "Previous company must be between 2 to 100 characters")
    private String previousCompany;

    @Pattern(regexp = "^\\d{22}$", message = "PF number must be exactly 22 digits")
    private String pfNumber;

    @DecimalMin(value = "0.0", message = "Salary must be positive")
    private Double salary;

    @Size(min = 10, max = 1000, message = "Permanent address must be between 10 to 1000 characters")
    private String permanentAddress;

    @Builder.Default
    private Boolean active = true;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDate;

    @Size(min = 3, max = 20, message = "Role must be between 3 to 20 characters")
    private String role;

    @Size(min = 10, max = 1000, message = "Address must be between 10 to 1000 characters")
    private String address;


    // Utility methods
    public String getContactNumber() {
        return mobileNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.mobileNumber = contactNumber;
    }
}
