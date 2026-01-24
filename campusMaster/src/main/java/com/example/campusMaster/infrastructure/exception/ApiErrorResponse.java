package com.example.campusMaster.infrastructure.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ApiErrorResponse {

    private boolean success;
    private String message;
    private Map<String, String> errors;
    private LocalDateTime timestamp;
}

