package com.example.campusMaster.application.dto.request.modules;

import java.util.List;

import com.example.campusMaster.application.dto.request.courses.UpdateCourseDTO;

import lombok.Data;

@Data
public class UpdateModuleDTO {
    private Long id; // null = nouveau module
    private String name;
    private String code;
    private String semestre;
    private List<UpdateCourseDTO> courses;
}
