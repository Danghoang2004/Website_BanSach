package com.example.web_bansach.common.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        /**
         * Xử lý ResourceNotFoundException
         */
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleNotFound(
                        ResourceNotFoundException ex,
                        WebRequest request) {

                logger.warn("Resource not found: {}", ex.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                "RESOURCE_NOT_FOUND",
                                ex.getMessage(),
                                HttpStatus.NOT_FOUND.value(),
                                LocalDateTime.now(),
                                request.getDescription(false).replace("uri=", ""));

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        /**
         * Xử lý BadCredentialsException
         */
        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleAuth(
                        BadCredentialsException ex,
                        WebRequest request) {

                logger.warn("Authentication failed");

                ErrorResponse errorResponse = new ErrorResponse(
                                "INVALID_CREDENTIALS",
                                "Sai tên đăng nhập hoặc mật khẩu",
                                HttpStatus.UNAUTHORIZED.value(),
                                LocalDateTime.now(),
                                request.getDescription(false).replace("uri=", ""));

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        /**
         * Xử lý BusinessException
         */
        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ErrorResponse> handleBusinessException(
                        BusinessException ex,
                        WebRequest request) {

                logger.warn("Business exception: {}", ex.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                "BUSINESS_ERROR",
                                ex.getMessage(),
                                HttpStatus.BAD_REQUEST.value(),
                                LocalDateTime.now(),
                                request.getDescription(false).replace("uri=", ""));

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        /**
         * Xử lý validation errors
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidation(
                        MethodArgumentNotValidException ex,
                        WebRequest request) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                logger.warn("Validation failed: {}", errors);

                ErrorResponse errorResponse = new ErrorResponse(
                                "VALIDATION_ERROR",
                                "Dữ liệu không hợp lệ",
                                HttpStatus.BAD_REQUEST.value(),
                                LocalDateTime.now(),
                                request.getDescription(false).replace("uri=", ""));
                errorResponse.setDetails(errors);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        /**
         * Xử lý IllegalArgumentException
         */
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleBadRequest(
                        IllegalArgumentException ex,
                        WebRequest request) {

                logger.warn("Invalid argument: {}", ex.getMessage());

                ErrorResponse errorResponse = new ErrorResponse(
                                "INVALID_ARGUMENT",
                                ex.getMessage(),
                                HttpStatus.BAD_REQUEST.value(),
                                LocalDateTime.now(),
                                request.getDescription(false).replace("uri=", ""));

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        /**
         * Xử lý tất cả các exceptions khác
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleAll(
                        Exception ex,
                        WebRequest request) {

                logger.error("Unexpected error: {}", ex.getMessage(), ex);

                ErrorResponse errorResponse = new ErrorResponse(
                                "INTERNAL_SERVER_ERROR",
                                "Lỗi hệ thống, vui lòng thử lại sau",
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                LocalDateTime.now(),
                                request.getDescription(false).replace("uri=", ""));

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
}
