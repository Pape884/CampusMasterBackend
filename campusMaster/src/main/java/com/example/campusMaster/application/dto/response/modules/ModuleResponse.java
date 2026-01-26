package com.example.campusMaster.application.dto.response.modules;

import java.util.List;

import com.example.campusMaster.application.dto.response.courses.CourseResponse;
import com.example.campusMaster.application.dto.response.departments.DepartmentResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ModuleResponse {
    private Long id;
    private String code;
    private String name;
    private String semestre;
    DepartmentResponse department;
    Integer coursesCount;
    private List<CourseResponse> courses;
}
