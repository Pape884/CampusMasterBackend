package com.example.campusMaster.application.dto.response;

import java.time.LocalDateTime;

public record EnrollmentResponse(
    Long id,
    CourseResponse course,
    LocalDateTime enrolledAt,
    Boolean isActive,
    Double finalGrade
) {}
