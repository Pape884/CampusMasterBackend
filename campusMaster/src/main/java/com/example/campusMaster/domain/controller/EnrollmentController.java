package com.example.campusMaster.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.campusMaster.application.Services.EnrollmentService;
import com.example.campusMaster.application.Services.UserService;
import com.example.campusMaster.application.dto.request.EnrollmentRequest;
import com.example.campusMaster.application.dto.response.ApiResponse;
import com.example.campusMaster.application.dto.response.EnrollmentResponse;
import com.example.campusMaster.application.dto.response.UserResponse;
import com.example.campusMaster.infrastructure.security.SecurityUtils;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Enrollments", description = "Gestion des inscriptions aux cours")
public class EnrollmentController {
    
    private final EnrollmentService enrollmentService;
    private final UserService userService;
    
    @Operation(summary = "S'inscrire à un cours (STUDENT)")
    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollToCourse(
            @Valid @RequestBody EnrollmentRequest request
    ) {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        
        EnrollmentResponse enrollment = enrollmentService.enrollStudent(
            currentUser.id(), 
            request.courseId()
        );
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(enrollment, "Inscription réussie"));
    }
    
    @Operation(summary = "Se désinscrire d'un cours (STUDENT)")
    @DeleteMapping("/{enrollmentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> unenrollFromCourse(
            @PathVariable Long enrollmentId
    ) {
        enrollmentService.unenrollStudent(enrollmentId);
        return ResponseEntity.ok(ApiResponse.success(null, "Désinscription réussie"));
    }
    
    @Operation(summary = "Lister mes inscriptions (STUDENT)")
    @GetMapping("/my-enrollments")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<EnrollmentResponse> enrollments = 
            enrollmentService.getEnrollmentsByStudent(currentUser.id());
        return ResponseEntity.ok(ApiResponse.success(enrollments));
    }
    
    @Operation(summary = "Lister les inscriptions d'un cours (TEACHER/ADMIN)")
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getEnrollmentsByCourse(
            @PathVariable Long courseId
    ) {
        List<EnrollmentResponse> enrollments = 
            enrollmentService.getEnrollmentsByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(enrollments));
    }
    
    @Operation(summary = "Nombre d'inscrits à un cours")
    @GetMapping("/course/{courseId}/count")
    public ResponseEntity<ApiResponse<Long>> countEnrollmentsByCourse(
            @PathVariable Long courseId
    ) {
        Long count = enrollmentService.countEnrollmentsByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}

