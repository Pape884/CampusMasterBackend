package com.example.campusMaster.application.dto.response.modules;

import java.util.List;

import com.example.campusMaster.application.dto.response.courses.CourseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModuleResponse {
    private Long id;
    private String code;
    private String name;
    private String semestre;
    private Long departmentId;
    Integer coursesCount;
    private List<CourseResponse> courses;
}
