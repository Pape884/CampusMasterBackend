package com.example.campusMaster.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
    @NotNull(message = "Course ID obligatoire")
    Long courseId) {}
