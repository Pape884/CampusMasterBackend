package com.example.campusMaster.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateGradeRequest(
    @NotNull(message = "Points obligatoire")
    Double points,
    
    String feedback
) {}
