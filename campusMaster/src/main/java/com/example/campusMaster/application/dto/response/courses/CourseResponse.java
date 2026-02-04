package com.example.campusMaster.application.dto.response.courses;
import java.time.LocalDateTime;

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
    Integer credits;
    String status;
    Boolean isActive;
    Long moduleId;
    Long teacherId;
    LocalDateTime createdAt;
}
    

