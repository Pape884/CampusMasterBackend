package com.example.campusMaster.application.dto.request.courses;

import lombok.Data;

@Data
public class UpdateCourseDTO {
    private Long id; // null = nouveau cours
    private String titre;
    private String description;
    private Integer credits;
    private String status;
    private Boolean isActive;
}

