package com.hr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Generic API Response DTO
 * Provides consistent response structure for all API endpoints
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO<T> {

    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private String errorCode;
    private String path;
    private Integer statusCode;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    // Pagination information (when applicable)
    private PaginationDTO pagination;

    // Meta information
    private Object meta;

    // Default constructor
    public ApiResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for success response
    public ApiResponseDTO(boolean success, String message, T data) {
        this();
        this.success = success;
        this.message = message;
        this.data = data;
        this.statusCode = 200;
    }

    // Constructor for error response
    public ApiResponseDTO(boolean success, String message, List<String> errors, Integer statusCode) {
        this();
        this.success = success;
        this.message = message;
        this.errors = errors;
        this.statusCode = statusCode;
    }

    // Static factory methods for common responses
    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return new ApiResponseDTO<>(true, message, data);
    }

    public static <T> ApiResponseDTO<T> success(T data) {
        return new ApiResponseDTO<>(true, "Success", data);
    }

    public static <T> ApiResponseDTO<T> success(String message) {
        return new ApiResponseDTO<>(true, message, null);
    }

    public static <T> ApiResponseDTO<T> error(String message) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.success = false;
        response.message = message;
        response.statusCode = 400;
        return response;
    }

    public static <T> ApiResponseDTO<T> error(String message, Integer statusCode) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.success = false;
        response.message = message;
        response.statusCode = statusCode;
        return response;
    }

    public static <T> ApiResponseDTO<T> error(String message, List<String> errors) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.success = false;
        response.message = message;
        response.errors = errors;
        response.statusCode = 400;
        return response;
    }

    public static <T> ApiResponseDTO<T> error(String message, List<String> errors, Integer statusCode) {
        return new ApiResponseDTO<>(false, message, errors, statusCode);
    }

    // Static factory methods for specific HTTP status responses
    public static <T> ApiResponseDTO<T> created(String message, T data) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>(true, message, data);
        response.statusCode = 201;
        return response;
    }

    public static <T> ApiResponseDTO<T> notFound(String message) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.success = false;
        response.message = message;
        response.statusCode = 404;
        return response;
    }

    public static <T> ApiResponseDTO<T> unauthorized(String message) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.success = false;
        response.message = message != null ? message : "Unauthorized access";
        response.statusCode = 401;
        return response;
    }

    public static <T> ApiResponseDTO<T> forbidden(String message) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.success = false;
        response.message = message != null ? message : "Access forbidden";
        response.statusCode = 403;
        return response;
    }

    public static <T> ApiResponseDTO<T> internalServerError(String message) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.success = false;
        response.message = message != null ? message : "Internal server error";
        response.statusCode = 500;
        return response;
    }

    // Method to add pagination
    public ApiResponseDTO<T> withPagination(PaginationDTO pagination) {
        this.pagination = pagination;
        return this;
    }

    // Method to add meta information
    public ApiResponseDTO<T> withMeta(Object meta) {
        this.meta = meta;
        return this;
    }

    // Method to add path information
    public ApiResponseDTO<T> withPath(String path) {
        this.path = path;
        return this;
    }

    // Method to add error code
    public ApiResponseDTO<T> withErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public PaginationDTO getPagination() {
        return pagination;
    }

    public void setPagination(PaginationDTO pagination) {
        this.pagination = pagination;
    }

    public Object getMeta() {
        return meta;
    }

    public void setMeta(Object meta) {
        this.meta = meta;
    }

    // Utility methods
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public boolean hasPagination() {
        return pagination != null;
    }

    public boolean hasMeta() {
        return meta != null;
    }

    @Override
    public String toString() {
        return "ApiResponseDTO{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", statusCode=" + statusCode +
                ", timestamp=" + timestamp +
                '}';
    }

    /**
     * Inner class for pagination information
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PaginationDTO {
        private Integer currentPage;
        private Integer totalPages;
        private Long totalElements;
        private Integer pageSize;
        private Boolean hasNext;
        private Boolean hasPrevious;
        private Boolean isFirst;
        private Boolean isLast;

        public PaginationDTO() {}

        public PaginationDTO(Integer currentPage, Integer totalPages, Long totalElements, Integer pageSize) {
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalElements = totalElements;
            this.pageSize = pageSize;
            this.hasNext = currentPage < totalPages;
            this.hasPrevious = currentPage > 1;
            this.isFirst = currentPage == 1;
            this.isLast = currentPage.equals(totalPages);
        }

        // Getters and Setters
        public Integer getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(Integer currentPage) {
            this.currentPage = currentPage;
        }

        public Integer getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(Integer totalPages) {
            this.totalPages = totalPages;
        }

        public Long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(Long totalElements) {
            this.totalElements = totalElements;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }

        public Boolean getHasNext() {
            return hasNext;
        }

        public void setHasNext(Boolean hasNext) {
            this.hasNext = hasNext;
        }

        public Boolean getHasPrevious() {
            return hasPrevious;
        }

        public void setHasPrevious(Boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
        }

        public Boolean getIsFirst() {
            return isFirst;
        }

        public void setIsFirst(Boolean isFirst) {
            this.isFirst = isFirst;
        }

        public Boolean getIsLast() {
            return isLast;
        }

        public void setIsLast(Boolean isLast) {
            this.isLast = isLast;
        }
    }
}
