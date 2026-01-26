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
import com.example.campusMaster.application.dto.request.announcement.CreateAnnouncementRequest;
import com.example.campusMaster.application.dto.response.announcement.AnnouncementResponse;
import com.example.campusMaster.application.dto.response.users.UserResponse;
import com.example.campusMaster.infrastructure.exception.ApiSuccessResponse;
import com.example.campusMaster.infrastructure.security.SecurityUtils;

import java.time.LocalDateTime;
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
    public ResponseEntity<ApiSuccessResponse> createAnnouncement(
            @Valid @RequestBody CreateAnnouncementRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);

        AnnouncementResponse announcement = announcementService.createAnnouncement(
                request,
                currentUser.id());

        ApiSuccessResponse<AnnouncementResponse> response = ApiSuccessResponse.<AnnouncementResponse>builder()
                .success(true)
                .message("Annonce créée")
                .data(announcement)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @Operation(summary = "Modifier une annonce (TEACHER/ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiSuccessResponse<AnnouncementResponse>> updateAnnouncement(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content) {
        AnnouncementResponse announcement = announcementService.updateAnnouncement(
                id,
                title,
                content);

        ApiSuccessResponse<AnnouncementResponse> response = ApiSuccessResponse.<AnnouncementResponse>builder()
                .success(true)
                .message("Annonce mise à jour")
                .data(announcement)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Récupérer une annonce par ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<AnnouncementResponse>> getAnnouncementById(
            @PathVariable Long id) {
        AnnouncementResponse announcement = announcementService.getAnnouncementById(id);
        ApiSuccessResponse<AnnouncementResponse> response = ApiSuccessResponse.<AnnouncementResponse>builder()
                .success(true)
                .message("Annonce récupérée")
                .data(announcement)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lister les annonces d'un cours")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiSuccessResponse<List<AnnouncementResponse>>> getAnnouncementsByCourse(
            @PathVariable Long courseId) {
        List<AnnouncementResponse> announcements = announcementService.getAnnouncementsByCourse(courseId);
        ApiSuccessResponse<List<AnnouncementResponse>> response = ApiSuccessResponse.<List<AnnouncementResponse>>builder()
                .success(true)
                .message("Annonces récupérées")
                .data(announcements)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lister toutes les annonces épinglées")
    @GetMapping("/pinned")
    public ResponseEntity<ApiSuccessResponse<List<AnnouncementResponse>>> getPinnedAnnouncements() {
        List<AnnouncementResponse> announcements = announcementService.getPinnedAnnouncements();
        ApiSuccessResponse<List<AnnouncementResponse>> response = ApiSuccessResponse.<List<AnnouncementResponse>>builder()
                .success(true)
                .message("Annonces épinglées récupérées")
                .data(announcements)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Épingler/Désépingler une annonce (TEACHER/ADMIN)")
    @PutMapping("/{id}/pin")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiSuccessResponse<String>> togglePin(
            @PathVariable Long id,
            @RequestParam boolean pinned) {
        announcementService.togglePin(id, pinned);
        String message = pinned ? "Annonce épinglée" : "Annonce désépinglée";
        ApiSuccessResponse<String> response = ApiSuccessResponse.<String>builder()
                .success(true)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Supprimer une annonce (TEACHER/ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiSuccessResponse<String>> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        ApiSuccessResponse<String> response = ApiSuccessResponse.<String>builder()
                .success(true)
                .message("Annonce supprimée")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}
