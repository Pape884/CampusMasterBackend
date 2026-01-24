package com.example.campusMaster.application.dto.response.departments;

import java.time.LocalDateTime;
import java.util.List;

import com.example.campusMaster.domain.entity.CourseModule;

import lombok.Builder;
import lombok.Getter;

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
     List<CourseModule> modules;
    
}