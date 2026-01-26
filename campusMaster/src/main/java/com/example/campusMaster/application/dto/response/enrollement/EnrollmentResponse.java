package com.example.campusMaster.application.dto.response.enrollement;

import java.time.LocalDateTime;

import com.example.campusMaster.application.dto.response.courses.CourseResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EnrollmentResponse {
    private Long id;
    private CourseResponse course;
    private LocalDateTime enrolledAt;
    private Boolean isActive;
    private Double finalGrade;

}

