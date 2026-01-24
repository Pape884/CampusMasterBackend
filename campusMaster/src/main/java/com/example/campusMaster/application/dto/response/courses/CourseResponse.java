package com.example.campusMaster.application.dto.response.courses;
import java.time.LocalDateTime;

import com.example.campusMaster.application.dto.response.users.UserResponse;

import lombok.Builder;
import lombok.Getter;



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
    UserResponse teacher;
    Integer studentsCount;
    LocalDateTime createdAt;
}
    

