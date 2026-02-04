package com.example.campusMaster.application.dto.request.courses;
import jakarta.validation.constraints.NotBlank;

public record CreateCourseRequest(
    @NotBlank(message = "Code obligatoire")
    String code,
    
    @NotBlank(message = "Titre obligatoire")
    String titre,
    
    String description,
    
    Integer credits,
    
    String status,

    Long moduleId

) {}

