package com.example.web_bansach.common.exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Unified error response structure for all API responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String code; // Error code (e.g., "RESOURCE_NOT_FOUND")
    private String message; // Error message
    private int status; // HTTP status code
    private LocalDateTime timestamp; // When the error occurred
    private String path; // Request path
    private Map<String, String> details; // Additional error details (e.g., field validation errors)

    public ErrorResponse(String code, String message, int status, LocalDateTime timestamp, String path) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.path = path;
    }
}

