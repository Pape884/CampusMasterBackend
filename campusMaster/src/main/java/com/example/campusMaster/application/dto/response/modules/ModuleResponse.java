package com.example.campusMaster.application.dto.response.modules;

import java.util.List;

import com.example.campusMaster.application.dto.response.courses.CourseResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ModuleResponse {
    private Long id;
    private String code;
    private String name;
    private String semestre;
    private Long departmentId;
    private List<CourseResponse> courses;
}
