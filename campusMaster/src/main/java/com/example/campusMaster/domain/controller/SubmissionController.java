package com.example.campusMaster.domain.controller;

import com.example.campusMaster.application.dto.response.submissions.SubmissionResponse;
import com.example.campusMaster.application.dto.response.users.UserResponse;
import com.example.campusMaster.application.Services.SubmissionService;
import com.example.campusMaster.application.Services.UserService;
import com.example.campusMaster.infrastructure.exception.ApiSuccessResponse;
import com.example.campusMaster.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Submissions", description = "Gestion des rendus de devoirs")
public class SubmissionController {
    
    private final SubmissionService submissionService;
    private final UserService userService;
    
    @Operation(summary = "Soumettre un devoir (STUDENT)")
    @PostMapping("/assignment/{assignmentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiSuccessResponse<SubmissionResponse>> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String comments
    ) {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        
        SubmissionResponse submission = submissionService.submitAssignment(
            assignmentId, 
            currentUser.id(), 
            file, 
            comments
        );

        ApiSuccessResponse<SubmissionResponse> response = ApiSuccessResponse.<SubmissionResponse>builder()
                .success(true)
                .message("Devoir soumis avec succès")
                .data(submission)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    
    @Operation(summary = "Mettre à jour une soumission (nouvelle version)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiSuccessResponse<SubmissionResponse>> updateSubmission(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String comments
    ) {
        SubmissionResponse submission = submissionService.updateSubmission(id, file, comments);

        ApiSuccessResponse<SubmissionResponse> response = ApiSuccessResponse.<SubmissionResponse>builder()
                .success(true)
                .message("Soumission mise à jour avec succès")
                .data(submission)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Récupérer une soumission par ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<SubmissionResponse>> getSubmissionById(
            @PathVariable Long id
    ) {
        SubmissionResponse submission = submissionService.getSubmissionById(id);

        ApiSuccessResponse<SubmissionResponse> response = ApiSuccessResponse.<SubmissionResponse>builder()
                .success(true)
                .message("Soumission récupérée avec succès")
                .data(submission)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Lister les soumissions d'un devoir (TEACHER/ADMIN)")
    @GetMapping("/assignment/{assignmentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiSuccessResponse<List<SubmissionResponse>>> getSubmissionsByAssignment(
            @PathVariable Long assignmentId
    ) {
        List<SubmissionResponse> submissions = 
            submissionService.getSubmissionsByAssignment(assignmentId);

            ApiSuccessResponse<List<SubmissionResponse>> response = ApiSuccessResponse.<List<SubmissionResponse>>builder()
                    .success(true)
                    .message("Soumissions récupérées avec succès")
                    .data(submissions)
                    .timestamp(java.time.LocalDateTime.now())
                    .build();
            return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Lister mes soumissions (STUDENT)")
    @GetMapping("/my-submissions")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiSuccessResponse<List<SubmissionResponse>>> getMySubmissions() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<SubmissionResponse> submissions = 
            submissionService.getSubmissionsByStudent(currentUser.id());

            ApiSuccessResponse<List<SubmissionResponse>> response = ApiSuccessResponse.<List<SubmissionResponse>>builder()
                    .success(true)
                    .message("Soumissions récupérées avec succès")
                    .data(submissions)
                    .timestamp(java.time.LocalDateTime.now())
                    .build();
            return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Retirer une soumission (STUDENT)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiSuccessResponse<String>> withdrawSubmission(@PathVariable Long id) {
        submissionService.withdrawSubmission(id);

        ApiSuccessResponse<String> response = ApiSuccessResponse.<String>builder()
                .success(true)
                .message("Soumission retirée")
                .data(null)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}

