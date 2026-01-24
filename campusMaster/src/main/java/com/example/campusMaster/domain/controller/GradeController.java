package com.example.campusMaster.domain.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.campusMaster.application.Services.GradeService;
import com.example.campusMaster.application.Services.UserService;
import com.example.campusMaster.application.dto.request.CreateGradeRequest;
import com.example.campusMaster.application.dto.response.ApiResponse;
import com.example.campusMaster.application.dto.response.GradeResponse;
import com.example.campusMaster.application.dto.response.UserResponse;
import com.example.campusMaster.infrastructure.security.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Grades", description = "Gestion des notes")
public class GradeController {
    
    private final GradeService gradeService;
    private final UserService userService;
    
    @Operation(summary = "Noter une soumission (TEACHER/ADMIN)")
    @PostMapping("/submission/{submissionId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<GradeResponse>> gradeSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody CreateGradeRequest request
    ) {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        
        GradeResponse grade = gradeService.gradeSubmission(
            submissionId, 
            currentUser.id(), 
            request
        );
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(grade, "Note attribuée"));
    }
    
    @Operation(summary = "Modifier une note (TEACHER/ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<GradeResponse>> updateGrade(
            @PathVariable Long id,
            @Valid @RequestBody CreateGradeRequest request
    ) {
        GradeResponse grade = gradeService.updateGrade(id, request);
        return ResponseEntity.ok(ApiResponse.success(grade, "Note mise à jour"));
    }
    
    @Operation(summary = "Récupérer une note par ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GradeResponse>> getGradeById(@PathVariable Long id) {
        GradeResponse grade = gradeService.getGradeById(id);
        return ResponseEntity.ok(ApiResponse.success(grade));
    }
    
    @Operation(summary = "Lister mes notes (STUDENT)")
    @GetMapping("/my-grades")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<GradeResponse>>> getMyGrades() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<GradeResponse> grades = gradeService.getGradesByStudent(currentUser.id());
        return ResponseEntity.ok(ApiResponse.success(grades));
    }
    
    @Operation(summary = "Lister les notes d'un étudiant (TEACHER/ADMIN)")
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<GradeResponse>>> getGradesByStudent(
            @PathVariable Long studentId
    ) {
        List<GradeResponse> grades = gradeService.getGradesByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(grades));
    }
    
    @Operation(summary = "Lister les notes d'un cours (TEACHER/ADMIN)")
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<GradeResponse>>> getGradesByCourse(
            @PathVariable Long courseId
    ) {
        List<GradeResponse> grades = gradeService.getGradesByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(grades));
    }
    
    @Operation(summary = "Moyenne d'un étudiant")
    @GetMapping("/student/{studentId}/average")
    public ResponseEntity<ApiResponse<Double>> getStudentAverage(
            @PathVariable Long studentId
    ) {
        Double average = gradeService.getStudentAverage(studentId);
        return ResponseEntity.ok(ApiResponse.success(average));
    }
}