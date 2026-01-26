package com.example.campusMaster.domain.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.campusMaster.application.Services.MessageService;
import com.example.campusMaster.application.Services.UserService;
import com.example.campusMaster.application.dto.request.message.SendMessageRequest;
import com.example.campusMaster.application.dto.response.message.MessageResponse;
import com.example.campusMaster.application.dto.response.users.UserResponse;
import com.example.campusMaster.infrastructure.exception.ApiSuccessResponse;
import com.example.campusMaster.infrastructure.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Messages", description = "Messagerie interne")
public class MessageController {
    
    private final MessageService messageService;
    private final UserService userService;
    
    @Operation(summary = "Envoyer un message")
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<MessageResponse>> sendMessage(
            @Valid @RequestBody SendMessageRequest request
    ) {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        
        MessageResponse message = messageService.sendMessage(
            currentUser.id(), 
            request.receiverId(), 
            request.subject(), 
            request.content(), 
            request.tags()
        );
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiSuccessResponse.<MessageResponse>builder()
                        .success(true)
                        .message("Message envoyé")
                        .data(message)
                        .timestamp(LocalDateTime.now())
                        .build());
    }
    
    @Operation(summary = "Lister mes messages reçus")
    @GetMapping("/received")
    public ResponseEntity<ApiSuccessResponse<List<MessageResponse>>> getReceivedMessages() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<MessageResponse> messages = messageService.getReceivedMessages(currentUser.id());
        return ResponseEntity.ok(ApiSuccessResponse.<List<MessageResponse>>builder()
                .success(true)
                .message("Messages reçus récupérés")
                .data(messages)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Lister mes messages envoyés")
    @GetMapping("/sent")
    public ResponseEntity<ApiSuccessResponse<List<MessageResponse>>> getSentMessages() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<MessageResponse> messages = messageService.getSentMessages(currentUser.id());
        return ResponseEntity.ok(ApiSuccessResponse.<List<MessageResponse>>builder()
                .success(true)
                .message("Messages envoyés récupérés")
                .data(messages)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Lister mes messages non lus")
    @GetMapping("/unread")
    public ResponseEntity<ApiSuccessResponse<List<MessageResponse>>> getUnreadMessages() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<MessageResponse> messages = messageService.getUnreadMessages(currentUser.id());
        return ResponseEntity.ok(ApiSuccessResponse.<List<MessageResponse>>builder()
                .success(true)
                .message("Messages non lus récupérés")
                .data(messages)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Nombre de messages non lus")
    @GetMapping("/unread/count")
    public ResponseEntity<ApiSuccessResponse<Long>> countUnreadMessages() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        Long count = messageService.countUnreadMessages(currentUser.id());
        return ResponseEntity.ok(ApiSuccessResponse.<Long>builder()
                .success(true)
                .message("Nombre de messages non lus récupéré")
                .data(count)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Marquer un message comme lu")
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiSuccessResponse<String>> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return ResponseEntity.ok(ApiSuccessResponse.<String>builder()
                .success(true)
                .message("Message marqué comme lu")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Conversation avec un utilisateur")
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<ApiSuccessResponse<List<MessageResponse>>> getConversation(
            @PathVariable Long userId
    ) {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<MessageResponse> messages = 
            messageService.getConversation(currentUser.id(), userId);
        return ResponseEntity.ok(ApiSuccessResponse.<List<MessageResponse>>builder()
                .success(true)
                .message("Conversation récupérée")
                .data(messages)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @Operation(summary = "Supprimer un message")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<String>> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.ok(ApiSuccessResponse.<String>builder()
                .success(true)
                .message("Message supprimé")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
