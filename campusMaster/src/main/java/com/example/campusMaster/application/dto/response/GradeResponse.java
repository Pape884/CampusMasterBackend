package com.example.campusMaster.application.dto.response;

import java.time.LocalDateTime;

public record GradeResponse(
    Long id,
    Double points,
    String feedback,
    LocalDateTime gradedAt,
    UserResponse gradedBy
) {}
