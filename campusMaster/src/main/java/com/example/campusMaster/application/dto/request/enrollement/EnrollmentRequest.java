package com.example.campusMaster.application.dto.request.enrollement;

import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
    @NotNull(message = "Module ID obligatoire")
    Long moduleId
) {}
