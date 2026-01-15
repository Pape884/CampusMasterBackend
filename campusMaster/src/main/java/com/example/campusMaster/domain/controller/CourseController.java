package com.example.campusMaster.domain.controller;

import com.example.campusMaster.application.dto.request.CreateCourseRequest;
import com.example.campusMaster.application.dto.request.UpdateCourseRequest;
import com.example.campusMaster.application.dto.response.ApiResponse;
import com.example.campusMaster.application.dto.response.CourseResponse;
import com.example.campusMaster.application.dto.response.UserResponse;
import com.example.campusMaster.application.Services.CourseService;
import com.example.campusMaster.application.Services.UserService;
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
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @Valid @RequestBody CreateCourseRequest request
    ) {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        CourseResponse course = courseService.createCourse(request, currentUser.id());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(course, "Cours créé avec succès"));
    }
    
    @Operation(summary = "Modifier un cours (TEACHER/ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCourseRequest request
    ) {
        CourseResponse course = courseService.updateCourse(id, request);
        return ResponseEntity.ok(ApiResponse.success(course, "Cours mis à jour"));
    }
    
    @Operation(summary = "Récupérer un cours par ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable Long id) {
        CourseResponse course = courseService.getCourseById(id);
        return ResponseEntity.ok(ApiResponse.success(course));
    }
    
    @Operation(summary = "Lister tous les cours")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses() {
        List<CourseResponse> courses = courseService.getAllCourses();
        return ResponseEntity.ok(ApiResponse.success(courses));
    }
    
    @Operation(summary = "Lister les cours d'un enseignant")
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCoursesByTeacher(
            @PathVariable Long teacherId
    ) {
        List<CourseResponse> courses = courseService.getCoursesByTeacher(teacherId);
        return ResponseEntity.ok(ApiResponse.success(courses));
    }
    
    @Operation(summary = "Lister mes cours (enseignant connecté)")
    @GetMapping("/my-courses")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getMyCourses() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<CourseResponse> courses = courseService.getCoursesByTeacher(currentUser.id());
        return ResponseEntity.ok(ApiResponse.success(courses));
    }
    
    @Operation(summary = "Lister les cours par semestre")
    @GetMapping("/semestre/{semestre}")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCoursesBySemestre(
            @PathVariable String semestre
    ) {
        List<CourseResponse> courses = courseService.getCoursesBySemestre(semestre);
        return ResponseEntity.ok(ApiResponse.success(courses));
    }
    
    @Operation(summary = "Supprimer un cours (ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Cours supprimé"));
    }
}

