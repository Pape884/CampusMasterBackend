package com.example.campusMaster.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.campusMaster.application.Services.NotificationService;
import com.example.campusMaster.application.Services.UserService;
import com.example.campusMaster.application.dto.response.ApiResponse;
import com.example.campusMaster.application.dto.response.NotificationResponse;
import com.example.campusMaster.application.dto.response.UserResponse;
import com.example.campusMaster.infrastructure.security.SecurityUtils;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notifications", description = "Gestion des notifications")
public class NotificationController {
    
    private final NotificationService notificationService;
    private final UserService userService;
    
    @Operation(summary = "Lister mes notifications")
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<NotificationResponse> notifications = 
            notificationService.getNotificationsByUser(currentUser.id());
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }
    
    @Operation(summary = "Lister mes notifications non lues")
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<NotificationResponse> notifications = 
            notificationService.getUnreadNotifications(currentUser.id());
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }
    
    @Operation(summary = "Nombre de notifications non lues")
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> countUnreadNotifications() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        Long count = notificationService.countUnreadNotifications(currentUser.id());
        return ResponseEntity.ok(ApiResponse.success(count));
    }
    
    @Operation(summary = "Marquer une notification comme lue")
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markNotificationAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Notification marquée comme lue"));
    }
    
    @Operation(summary = "Marquer toutes les notifications comme lues")
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        notificationService.markAllAsRead(currentUser.id());
        return ResponseEntity.ok(ApiResponse.success(null, "Toutes les notifications marquées comme lues"));
    }
    
    @Operation(summary = "Supprimer une notification")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Notification supprimée"));
    }
    
    @Operation(summary = "Supprimer toutes les notifications lues")
    @DeleteMapping("/read")
    public ResponseEntity<ApiResponse<String>> deleteReadNotifications() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        notificationService.deleteReadNotifications(currentUser.id());
        return ResponseEntity.ok(ApiResponse.success(null, "Notifications lues supprimées"));
    }
}
