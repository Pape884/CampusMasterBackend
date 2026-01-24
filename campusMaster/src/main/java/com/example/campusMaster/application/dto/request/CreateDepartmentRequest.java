package com.example.campusMaster.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateDepartmentRequest(
    @NotBlank(message = "Nom obligatoire")
    String name,
    
    @NotBlank(message = "Code obligatoire")
    String code,
    
    String description) {}
