package com.example.campusMaster.application.dto.request.enrollement;

import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
    @NotNull(message = "Course ID obligatoire")
    Long courseId) {}
