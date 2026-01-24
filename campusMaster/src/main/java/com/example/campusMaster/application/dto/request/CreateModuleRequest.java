package com.example.campusMaster.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateModuleRequest(
    @NotBlank(message = "Code obligatoire")
    String code,
    
    @NotBlank(message = "Nom obligatoire")
    String name,
    
    String description,
    
    @NotNull(message = "Semestre obligatoire")
    Integer semestre,
    
    @NotNull(message = "Department ID obligatoire")
    Long departmentId
) {}
