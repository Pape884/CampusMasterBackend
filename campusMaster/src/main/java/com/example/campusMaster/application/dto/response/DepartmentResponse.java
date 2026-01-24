package com.example.campusMaster.application.dto.response;

public record DepartmentResponse(
    Long id,
    String name,
    String code,
    String description,
    Integer modulesCount
) {}
