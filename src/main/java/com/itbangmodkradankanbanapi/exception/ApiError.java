package com.itbangmodkradankanbanapi.exception;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class ApiError {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private List<FieldError> errors;

    public ApiError(LocalDateTime timestamp, int status, String error, String message, List<FieldError> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.errors = errors;
    }

    @Data
    public static class FieldError {
        private String field;
        private String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

    }
}