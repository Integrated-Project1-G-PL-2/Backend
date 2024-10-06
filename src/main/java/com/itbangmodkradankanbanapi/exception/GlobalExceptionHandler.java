package com.itbangmodkradankanbanapi.exception;

import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleItemNotFoundException(ItemNotFoundException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(Timestamp.from(Instant.now()), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage(), request.getRequestURI(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ItemNotFoundForUpdateAndDelete.class)
    public ResponseEntity<ErrorResponse> handleItemNotFoundException(ItemNotFoundForUpdateAndDelete ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(Timestamp.from(Instant.now()), HttpStatus.NOT_FOUND.value(), null, ex.getMessage(), null, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(Timestamp.from(Instant.now()), HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), ex.getMessage(), request.getRequestURI(), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ApiError.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiError.FieldError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Validation failed for object='" + ex.getBindingResult().getObjectName() + "'. Error count: " + fieldErrors.size(),
                fieldErrors
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizeAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizeAccessException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                Timestamp.from(Instant.now()),
                ex.getStatusCode().value(),
                ex.getMessage(),
                ex.getReason(),
                null,
                request.getRequestURI()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(InvalidRequestField.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestBody(ResponseStatusException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                Timestamp.from(Instant.now()),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getReason(),
                null,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleNormalResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                Timestamp.from(Instant.now()),
                ex.getStatusCode().value(),
                ex.getStatusCode().toString(),
                ex.getReason(),
                null,
                request.getRequestURI()
        );
        return ResponseEntity.status(ex.getStatusCode().value()).body(errorResponse);
    }


//    @ExceptionHandler(Exception.class)  // Catch any general exception
//    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
//        // Log the full stack trace
//        ex.printStackTrace();
//
//        ErrorResponse errorResponse = new ErrorResponse(
//                Timestamp.from(Instant.now()),
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),  // Use 500 for internal errors
//                "Internal Server Error",
//                ex.getMessage(),  // Get the exception message
//                null,
//                request.getRequestURI()
//        );
//
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//    }

}