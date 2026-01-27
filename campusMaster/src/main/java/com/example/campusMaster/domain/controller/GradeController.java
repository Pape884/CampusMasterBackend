package com.example.campusMaster.domain.controller;

import java.time.LocalDateTime;
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
import com.example.campusMaster.application.dto.request.grade.CreateGradeRequest;
import com.example.campusMaster.application.dto.response.grade.GradeResponse;
import com.example.campusMaster.application.dto.response.users.UserResponse;
import com.example.campusMaster.infrastructure.exception.ApiSuccessResponse;
import com.example.campusMaster.infrastructure.security.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Grades", description = "Gestion des notes")
public class GradeController {
    
    private final GradeService gradeService;
    private final UserService userService;
    
    @Operation(summary = "Noter une soumission (TEACHER/ADMIN)")
    @PostMapping("/submission/{submissionId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiSuccessResponse<GradeResponse>> gradeSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody CreateGradeRequest request
    ) {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        
        GradeResponse grade = gradeService.gradeSubmission(
            submissionId, 
            currentUser.getId(), 
            request
        );
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiSuccessResponse.<GradeResponse>builder()
                        .success(true)
                        .message("Note attribuée")
                        .data(grade)
                        .timestamp(LocalDateTime.now())
                        .build());
    }
    
    @Operation(summary = "Modifier une note (TEACHER/ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiSuccessResponse<GradeResponse>> updateGrade(
            @PathVariable Long id,
            @Valid @RequestBody CreateGradeRequest request
    ) {
        GradeResponse grade = gradeService.updateGrade(id, request);
        return ResponseEntity.ok(ApiSuccessResponse.<GradeResponse>builder()
                .success(true)
                .message("Note mise à jour")
                .data(grade)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Récupérer une note par ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<GradeResponse>> getGradeById(@PathVariable Long id) {
        GradeResponse grade = gradeService.getGradeById(id);
        return ResponseEntity.ok(ApiSuccessResponse.<GradeResponse>builder()
                .success(true)
                .message("Note récupérée")
                .data(grade)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Lister mes notes (STUDENT)")
    @GetMapping("/my-grades")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiSuccessResponse<List<GradeResponse>>> getMyGrades() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<GradeResponse> grades = gradeService.getGradesByStudent(currentUser.getId());
        return ResponseEntity.ok(ApiSuccessResponse.<List<GradeResponse>>builder()
                .success(true)
                .message("Notes récupérées")
                .data(grades)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Lister les notes d'un étudiant (TEACHER/ADMIN)")
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiSuccessResponse<List<GradeResponse>>> getGradesByStudent(
            @PathVariable Long studentId
    ) {
        List<GradeResponse> grades = gradeService.getGradesByStudent(studentId);
        return ResponseEntity.ok(ApiSuccessResponse.<List<GradeResponse>>builder()
                .success(true)
                .message("Notes récupérées")
                .data(grades)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Lister les notes d'un cours (TEACHER/ADMIN)")
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiSuccessResponse<List<GradeResponse>>> getGradesByCourse(
            @PathVariable Long courseId
    ) {
        List<GradeResponse> grades = gradeService.getGradesByCourse(courseId);
        return ResponseEntity.ok(ApiSuccessResponse.<List<GradeResponse>>builder()
                .success(true)
                .message("Notes récupérées")
                .data(grades)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Moyenne d'un étudiant")
    @GetMapping("/student/{studentId}/average")
    public ResponseEntity<ApiSuccessResponse<Double>> getStudentAverage(
            @PathVariable Long studentId
    ) {
        Double average = gradeService.getStudentAverage(studentId);
        return ResponseEntity.ok(ApiSuccessResponse.<Double>builder()
                .success(true)
                .message("Moyenne récupérée")
                .data(average)
                .timestamp(LocalDateTime.now())
                .build());
    }
}