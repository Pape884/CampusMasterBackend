package com.example.campusMaster.domain.controller;

import com.example.campusMaster.application.dto.request.assignments.CreateAssignmentRequest;
import com.example.campusMaster.application.dto.request.assignments.UpdateAssignmentRequest;
import com.example.campusMaster.application.dto.response.assignments.AssignmentResponse;
import com.example.campusMaster.infrastructure.exception.ApiSuccessResponse;
import com.example.campusMaster.application.Services.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Assignments", description = "Gestion des devoirs")
public class AssignmentController {
    private final AssignmentService assignmentService;
    
    @Operation(summary = "Créer un devoir (TEACHER/ADMIN)")
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiSuccessResponse<AssignmentResponse>> createAssignment(
            @Valid @RequestBody CreateAssignmentRequest request
    ) {
        AssignmentResponse assignment = assignmentService.createAssignment(request);

         ApiSuccessResponse<AssignmentResponse> response = ApiSuccessResponse.<AssignmentResponse>builder()
                .success(true)
                .message("Assignment created successfully")
                .data(assignment)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    
    @Operation(summary = "Modifier un devoir (TEACHER/ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiSuccessResponse<AssignmentResponse>> updateAssignment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAssignmentRequest request
    ) {
        AssignmentResponse assignment = assignmentService.updateAssignment(id, request);
        
        ApiSuccessResponse<AssignmentResponse> response = ApiSuccessResponse.<AssignmentResponse>builder()
                .success(true)
                .message("Assignment updated successfully")
                .data(assignment)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Récupérer un devoir par ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<AssignmentResponse>> getAssignmentById(
            @PathVariable Long id
    ) {
        AssignmentResponse assignment = assignmentService.getAssignmentById(id);
        ApiSuccessResponse<AssignmentResponse> response = ApiSuccessResponse.<AssignmentResponse>builder()
                .success(true)
                .message("Assignment retrieved successfully")
                .data(assignment)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Lister les devoirs d'un cours")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiSuccessResponse<List<AssignmentResponse>>> getAssignmentsByCourse(
            @PathVariable Long courseId
    ) {
        List<AssignmentResponse> assignments = assignmentService.getAssignmentsByCourseId(courseId);
        ApiSuccessResponse<List<AssignmentResponse>> response = ApiSuccessResponse.<List<AssignmentResponse>>builder()
                .success(true)
                .message("Assignments retrieved successfully")
                .data(assignments)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Lister les devoirs à venir")
    @GetMapping("/upcoming")
    public ResponseEntity<ApiSuccessResponse<List<AssignmentResponse>>> getUpcomingAssignments() {
        List<AssignmentResponse> assignments = assignmentService.getUpcomingAssignments();

        ApiSuccessResponse<List<AssignmentResponse>> response = ApiSuccessResponse.<List<AssignmentResponse>>builder()
                .success(true)
                .message("Upcoming assignments retrieved successfully")
                .data(assignments)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Supprimer un devoir (TEACHER/ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiSuccessResponse<String>> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);

        ApiSuccessResponse<String> response = ApiSuccessResponse.<String>builder()
                .success(true)
                .message("Devoir supprimé")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

}
