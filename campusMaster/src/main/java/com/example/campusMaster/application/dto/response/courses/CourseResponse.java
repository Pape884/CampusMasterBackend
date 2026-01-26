package com.example.campusMaster.application.dto.response.courses;
import java.time.LocalDateTime;

import com.example.campusMaster.application.dto.response.users.UserResponse;
import com.example.campusMaster.domain.entity.CourseModule;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;



@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder  
public class CourseResponse {

    Long id;
    String code;
    String titre;
    String description;
    String semestre;
    Integer annee;
    Boolean isActive;
    CourseModule module;
    UserResponse teacher;
    Integer credits;
    Integer studentsCount;
    LocalDateTime createdAt;
}
    

