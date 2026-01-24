package com.example.campusMaster.application.dto.request.departments;

import jakarta.validation.constraints.NotBlank;


public record CreateDepartmentRequest(

    @NotBlank(message = "Code obligatoire")
    String code,
    
    @NotBlank(message = "Titre obligatoire")
    String name,

    @NotBlank(message = "Description obligatoire")
    String description
) {}
