package com.example.campusMaster.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.campusMaster.application.Services.NotificationService;
import com.example.campusMaster.application.Services.UserService;
import com.example.campusMaster.application.dto.response.notification.NotificationResponse;
import com.example.campusMaster.application.dto.response.users.UserResponse;
import com.example.campusMaster.infrastructure.exception.ApiSuccessResponse;
import com.example.campusMaster.infrastructure.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notifications", description = "Gestion des notifications")
public class NotificationController {
    
    private final NotificationService notificationService;
    private final UserService userService;
    
    @Operation(summary = "Lister mes notifications")
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<NotificationResponse>>> getMyNotifications() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<NotificationResponse> notifications = 
            notificationService.getNotificationsByUser(currentUser.getId());
        return ResponseEntity.ok(ApiSuccessResponse.<List<NotificationResponse>>builder()
                .success(true)
                .message("Liste des notifications")
                .data(notifications)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Lister mes notifications non lues")
    @GetMapping("/unread")
    public ResponseEntity<ApiSuccessResponse<List<NotificationResponse>>> getUnreadNotifications() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<NotificationResponse> notifications = 
            notificationService.getUnreadNotifications(currentUser.getId());

        return ResponseEntity.ok(ApiSuccessResponse.<List<NotificationResponse>>builder()
                .success(true)
                .message("Liste des notifications non lues")
                .data(notifications)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Nombre de notifications non lues")
    @GetMapping("/unread/count")
    public ResponseEntity<ApiSuccessResponse<Long>> countUnreadNotifications() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        Long count = notificationService.countUnreadNotifications(currentUser.getId());
        return ResponseEntity.ok(ApiSuccessResponse.<Long>builder()
                .success(true)
                .message("Nombre de notifications non lues")
                .data(count)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Marquer une notification comme lue")
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiSuccessResponse<String>> markNotificationAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiSuccessResponse.<String>builder()
                .success(true)
                .message("Notification marquée comme lue")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Marquer toutes les notifications comme lues")
    @PutMapping("/read-all")
    public ResponseEntity<ApiSuccessResponse<String>> markAllAsRead() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        notificationService.markAllAsRead(currentUser.getId());
        return ResponseEntity.ok(ApiSuccessResponse.<String>builder()
                .success(true)
                .message("Toutes les notifications marquées comme lues")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Supprimer une notification")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<String>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(ApiSuccessResponse.<String>builder()
                .success(true)
                .message("Notification supprimée")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }
    

    @Operation(summary = "Supprimer toutes les notifications lues")
    @DeleteMapping("/read")
    public ResponseEntity<ApiSuccessResponse<String>> deleteReadNotifications() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        notificationService.deleteReadNotifications(currentUser.getId());
        return ResponseEntity.ok(ApiSuccessResponse.<String>builder()
                .success(true)
                .message("Notifications lues supprimées")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

}