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

import com.example.campusMaster.application.Services.AnnouncementService;
import com.example.campusMaster.application.Services.UserService;
import com.example.campusMaster.application.dto.request.CreateAnnouncementRequest;
import com.example.campusMaster.application.dto.response.AnnouncementResponse;
import com.example.campusMaster.application.dto.response.ApiResponse;
import com.example.campusMaster.application.dto.response.UserResponse;
import com.example.campusMaster.infrastructure.security.SecurityUtils;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Announcements", description = "Gestion des annonces")
public class AnnouncementController {
    
    private final AnnouncementService announcementService;
    private final UserService userService;
    
    @Operation(summary = "Créer une annonce (TEACHER/ADMIN)")
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> createAnnouncement(
            @Valid @RequestBody CreateAnnouncementRequest request
    ) {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        
        AnnouncementResponse announcement = announcementService.createAnnouncement(
            request, 
            currentUser.id()
        );
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(announcement, "Annonce créée"));
    }
    
    @Operation(summary = "Modifier une annonce (TEACHER/ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> updateAnnouncement(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content
    ) {
        AnnouncementResponse announcement = announcementService.updateAnnouncement(
            id, 
            title, 
            content
        );
        return ResponseEntity.ok(ApiResponse.success(announcement, "Annonce mise à jour"));
    }
    
    @Operation(summary = "Récupérer une annonce par ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> getAnnouncementById(
            @PathVariable Long id
    ) {
        AnnouncementResponse announcement = announcementService.getAnnouncementById(id);
        return ResponseEntity.ok(ApiResponse.success(announcement));
    }
    
    @Operation(summary = "Lister les annonces d'un cours")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getAnnouncementsByCourse(
            @PathVariable Long courseId
    ) {
        List<AnnouncementResponse> announcements = 
            announcementService.getAnnouncementsByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(announcements));
    }
    
    @Operation(summary = "Lister toutes les annonces épinglées")
    @GetMapping("/pinned")
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getPinnedAnnouncements() {
        List<AnnouncementResponse> announcements = announcementService.getPinnedAnnouncements();
        return ResponseEntity.ok(ApiResponse.success(announcements));
    }
    
    @Operation(summary = "Épingler/Désépingler une annonce (TEACHER/ADMIN)")
    @PutMapping("/{id}/pin")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> togglePin(
            @PathVariable Long id,
            @RequestParam boolean pinned
    ) {
        announcementService.togglePin(id, pinned);
        String message = pinned ? "Annonce épinglée" : "Annonce désépinglée";
        return ResponseEntity.ok(ApiResponse.success(null, message));
    }
    
    @Operation(summary = "Supprimer une annonce (TEACHER/ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Annonce supprimée"));
    }
}
