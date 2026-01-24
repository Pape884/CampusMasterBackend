package com.example.campusMaster.application.dto.request.courses;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCourseRequest(
    @NotBlank(message = "Code obligatoire")
    String code,
    
    @NotBlank(message = "Titre obligatoire")
    String titre,
    
    String description,
    
    @NotBlank(message = "Semestre obligatoire")
    String semestre,
    
    @NotNull(message = "Année obligatoire")
    Integer annee,
    
    Long moduleId
) {}
