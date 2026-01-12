package com.example.campusMaster.application.dto.response;

import java.time.LocalDateTime;

public record AssignmentResponse(
     Long id,
    String courseId,
    String description,
    LocalDateTime deadline,
    Integer submissionsCount,
    LocalDateTime createdAt
) {}
