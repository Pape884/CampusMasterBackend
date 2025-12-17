package com.example.campusMaster.application.dto.response;
import java.time.LocalDateTime;

public record CourseResponse(
    Long id,
    String code,
    String titre,
    String description,
    String semestre,
    Integer annee,
    Boolean isActive,
    UserResponse teacher,
    Integer enrolledStudentsCount,
    LocalDateTime createdAt
) {}
