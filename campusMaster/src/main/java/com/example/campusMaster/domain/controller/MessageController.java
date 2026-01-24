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
import com.example.campusMaster.application.dto.request.SendMessageRequest;
import com.example.campusMaster.application.dto.response.ApiResponse;
import com.example.campusMaster.application.dto.response.MessageResponse;
import com.example.campusMaster.application.dto.response.UserResponse;
import com.example.campusMaster.infrastructure.security.SecurityUtils;

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
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
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
                .body(ApiResponse.success(message, "Message envoyé"));
    }
    
    @Operation(summary = "Lister mes messages reçus")
    @GetMapping("/received")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getReceivedMessages() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<MessageResponse> messages = messageService.getReceivedMessages(currentUser.id());
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
    
    @Operation(summary = "Lister mes messages envoyés")
    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getSentMessages() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<MessageResponse> messages = messageService.getSentMessages(currentUser.id());
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
    
    @Operation(summary = "Lister mes messages non lus")
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getUnreadMessages() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<MessageResponse> messages = messageService.getUnreadMessages(currentUser.id());
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
    
    @Operation(summary = "Nombre de messages non lus")
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> countUnreadMessages() {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        Long count = messageService.countUnreadMessages(currentUser.id());
        return ResponseEntity.ok(ApiResponse.success(count));
    }
    
    @Operation(summary = "Marquer un message comme lu")
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Message marqué comme lu"));
    }
    
    @Operation(summary = "Conversation avec un utilisateur")
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getConversation(
            @PathVariable Long userId
    ) {
        String email = SecurityUtils.getCurrentUserEmail();
        UserResponse currentUser = userService.getUserByEmail(email);
        List<MessageResponse> messages = 
            messageService.getConversation(currentUser.id(), userId);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
    
    @Operation(summary = "Supprimer un message")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Message supprimé"));
    }
}
