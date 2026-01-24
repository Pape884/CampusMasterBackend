package com.example.campusMaster.application.dto.request.courses;
import jakarta.validation.constraints.NotBlank;

public record UpdateCourseRequest(
    @NotBlank(message = "Titre obligatoire")
    String titre,
    
    String description,
    
    String semestre,
    
    Integer annee,
    
    Boolean isActive
) {}
