package com.example.campusMaster.domain.controller;

import com.example.campusMaster.application.dto.request.CreateAssignmentRequest;
import com.example.campusMaster.application.dto.request.UpdateAssignmentRequest;
import com.example.campusMaster.application.dto.response.ApiResponse;
import com.example.campusMaster.application.dto.response.AssignmentResponse;
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
    public ResponseEntity<ApiResponse<AssignmentResponse>> createAssignment(
            @Valid @RequestBody CreateAssignmentRequest request
    ) {
        AssignmentResponse assignment = assignmentService.createAssignment(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(assignment, "Devoir créé"));
    }
    
    @Operation(summary = "Modifier un devoir (TEACHER/ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<AssignmentResponse>> updateAssignment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAssignmentRequest request
    ) {
        AssignmentResponse assignment = assignmentService.updateAssignment(id, request);
        return ResponseEntity.ok(ApiResponse.success(assignment, "Devoir mis à jour"));
    }
    
    @Operation(summary = "Récupérer un devoir par ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssignmentResponse>> getAssignmentById(
            @PathVariable Long id
    ) {
        AssignmentResponse assignment = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(ApiResponse.success(assignment));
    }
    
    @Operation(summary = "Lister les devoirs d'un cours")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getAssignmentsByCourse(
            @PathVariable Long courseId
    ) {
        List<AssignmentResponse> assignments = assignmentService.getAssignmentsByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success(assignments));
    }
    
    @Operation(summary = "Lister les devoirs à venir")
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getUpcomingAssignments() {
        List<AssignmentResponse> assignments = assignmentService.getUpcomingAssignments();
        return ResponseEntity.ok(ApiResponse.success(assignments));
    }
    
    @Operation(summary = "Supprimer un devoir (TEACHER/ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Devoir supprimé"));
    }

}
