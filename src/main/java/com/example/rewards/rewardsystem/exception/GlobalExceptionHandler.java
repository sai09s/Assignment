package com.example.rewards.rewardsystem.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, jakarta.servlet.http.HttpServletRequest httpRequest) {
        logger.warn("ResourceNotFoundException: {} at {}", ex.getMessage(), httpRequest.getRequestURI());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                httpRequest.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex, WebRequest request, jakarta.servlet.http.HttpServletRequest httpRequest) {
        logger.warn("CustomException: {} at {}", ex.getMessage(), httpRequest.getRequestURI());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                httpRequest.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request, jakarta.servlet.http.HttpServletRequest httpRequest) {
        logger.error("Unhandled Exception: {} at {}", ex.getMessage(), httpRequest.getRequestURI(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage(),
                httpRequest.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex, jakarta.servlet.http.HttpServletRequest httpRequest) {
        var fieldErrors = ex.getBindingResult().getFieldErrors();
        var errors = fieldErrors.stream()
                .map(fieldError -> {
                    java.util.Map<String, String> err = new java.util.HashMap<>();
                    err.put("field", fieldError.getField());
                    err.put("message", fieldError.getDefaultMessage());
                    return err;
                })
                .toList();
        logger.warn("Validation error: {} at {}", errors, httpRequest.getRequestURI());
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("errors", errors);
        body.put("path", httpRequest.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, jakarta.servlet.http.HttpServletRequest httpRequest) {
        var violations = ex.getConstraintViolations().stream()
                .map(violation -> {
                    java.util.Map<String, String> err = new java.util.HashMap<>();
                    err.put("property", violation.getPropertyPath().toString());
                    err.put("message", violation.getMessage());
                    return err;
                })
                .toList();
        logger.warn("Constraint violation: {} at {}", violations, httpRequest.getRequestURI());
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("errors", violations);
        body.put("path", httpRequest.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
