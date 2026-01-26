package com.example.campusMaster.application.dto.request.grade;

import jakarta.validation.constraints.NotNull;

public record CreateGradeRequest(
    @NotNull(message = "Points obligatoire")
    Double points,
    
    String feedback
) {}
