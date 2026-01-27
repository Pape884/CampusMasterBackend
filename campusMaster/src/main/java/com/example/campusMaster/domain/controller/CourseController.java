package com.example.campusMaster.domain.controller;

import com.example.campusMaster.application.dto.request.courses.CreateCourseRequest;
import com.example.campusMaster.application.dto.request.courses.UpdateCourseRequest;
import com.example.campusMaster.application.dto.response.courses.CourseResponse;
import com.example.campusMaster.application.dto.response.users.UserResponse;
import com.example.campusMaster.application.Services.CourseService;
import com.example.campusMaster.application.Services.UserService;
import com.example.campusMaster.infrastructure.exception.ApiSuccessResponse;
import com.example.campusMaster.infrastructure.security.SecurityUtils;
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
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Courses", description = "Gestion des cours")
public class CourseController {
    
    private final CourseService courseService;
    private final UserService userService;
    
    @Operation(summary = "Créer un nouveau cours (TEACHER/ADMIN)")
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiSuccessResponse<CourseResponse>> createCourse(
            @Valid @RequestBody CreateCourseRequest request
    ) {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        CourseResponse course = courseService.createCourse(request, currentUser.getId());

        ApiSuccessResponse<CourseResponse> response = ApiSuccessResponse.<CourseResponse>builder()
                .success(true)
                .message("Cours créé avec succès")
                .data(course)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    
    @Operation(summary = "Modifier un cours (TEACHER/ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiSuccessResponse<CourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCourseRequest request
    ) {
        CourseResponse course = courseService.updateCourse(id, request);

        ApiSuccessResponse<CourseResponse> response = ApiSuccessResponse.<CourseResponse>builder()
                .success(true)
                .message("Cours mis à jour avec succès")
                .data(course)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Récupérer un cours par ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<CourseResponse>> getCourseById(@PathVariable Long id) {
        CourseResponse course = courseService.getCourseById(id);

        ApiSuccessResponse<CourseResponse> response = ApiSuccessResponse.<CourseResponse>builder()
                .success(true)
                .message("Cours récupéré avec succès")
                .data(course)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Lister tous les cours")
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<CourseResponse>>> getAllCourses() {
        List<CourseResponse> courses = courseService.getAllCourses();
        
        ApiSuccessResponse<List<CourseResponse>> response = ApiSuccessResponse.<List<CourseResponse>>builder()
                .success(true)
                .message("Cours récupérés avec succès")
                .data(courses)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Lister les cours d'un enseignant")
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<ApiSuccessResponse<List<CourseResponse>>> getCoursesByTeacher(
            @PathVariable Long teacherId
    ) {
        List<CourseResponse> courses = courseService.getCoursesByTeacher(teacherId);
        ApiSuccessResponse<List<CourseResponse>> response = ApiSuccessResponse.<List<CourseResponse>>builder()
                .success(true)
                .message("Cours récupérés avec succès")
                .data(courses)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Lister mes cours (enseignant connecté)")
    @GetMapping("/my-courses")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiSuccessResponse<List<CourseResponse>>> getMyCourses() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<CourseResponse> courses = courseService.getCoursesByTeacher(currentUser.getId());
        ApiSuccessResponse<List<CourseResponse>> response = ApiSuccessResponse.<List<CourseResponse>>builder()
                .success(true)
                .message("Cours récupérés avec succès")
                .data(courses)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Lister les cours par semestre")
    @GetMapping("/semestre/{semestre}")
    public ResponseEntity<ApiSuccessResponse<List<CourseResponse>>> getCoursesBySemestre(
            @PathVariable String semestre
    ) {
        List<CourseResponse> courses = courseService.getCoursesBySemestre(semestre);
        ApiSuccessResponse<List<CourseResponse>> response = ApiSuccessResponse.<List<CourseResponse>>builder()
                .success(true)
                .message("Cours récupérés avec succès")
                .data(courses)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Supprimer un cours (ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiSuccessResponse<String>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        ApiSuccessResponse<String> response = ApiSuccessResponse.<String>builder()
                .success(true)
                .message("Cours supprimé")
                .data(null)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}

