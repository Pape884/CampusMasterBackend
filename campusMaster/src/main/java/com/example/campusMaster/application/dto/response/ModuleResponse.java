package com.example.campusMaster.application.dto.response;

public record ModuleResponse(
    Long id,
    String code,
    String name,
    String description,
    Integer semestre,
    DepartmentResponse department,
    Integer coursesCount
) {}
