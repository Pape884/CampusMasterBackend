package com.example.campusMaster.application.dto.response.departments;

import java.time.LocalDateTime;
import java.util.List;

import com.example.campusMaster.application.dto.response.modules.ModuleResponse;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
public class DepartmentResponse {
     Long id;
     String name;
     String code;
     String description;
     Boolean isActive;
     LocalDateTime createdAt;
     LocalDateTime updatedAt;
     Long studentsCount;
     Long teachersCount;
     Long modulesCount;
     Long coursesCount;
     List<ModuleResponse> modules;

}