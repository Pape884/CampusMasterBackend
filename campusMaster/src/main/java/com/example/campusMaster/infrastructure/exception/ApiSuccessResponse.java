package com.example.campusMaster.infrastructure.exception;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiSuccessResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
}

