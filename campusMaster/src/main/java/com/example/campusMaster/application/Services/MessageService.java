package com.example.campusMaster.application.Services;

import com.example.campusMaster.application.dto.response.message.MessageResponse;
import com.example.campusMaster.application.dto.response.users.UserResponse;
import com.example.campusMaster.domain.entity.Message;
import com.example.campusMaster.domain.entity.User;
import com.example.campusMaster.infrastructure.persistence.repository.MessageRepository;
import com.example.campusMaster.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    
    /**
     * Envoyer un message
     */
    public MessageResponse sendMessage(Long senderId, Long receiverId, String subject, String content, String tags) {
        // Charger l'expéditeur
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Expéditeur introuvable"));
        
        // Charger le destinataire
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Destinataire introuvable"));
        
        // Vérifier que l'expéditeur n'envoie pas un message à lui-même
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("Vous ne pouvez pas vous envoyer un message à vous-même");
        }
        
        // Créer le message
        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .subject(subject)
                .content(content)
                .tags(tags)
                .isRead(false)
                .build();
        
        message.send();
        
        Message saved = messageRepository.save(message);
        return mapToResponse(saved);
    }
    
    /**
     * Récupérer un message par ID
     */
    @Transactional(readOnly = true)
    public MessageResponse getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message introuvable"));
        return mapToResponse(message);
    }
    
    /**
     * Récupérer tous les messages reçus par un utilisateur
     */
    @Transactional(readOnly = true)
    public List<MessageResponse> getReceivedMessages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        
        return messageRepository.findByReceiver(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer tous les messages envoyés par un utilisateur
     */
    @Transactional(readOnly = true)
    public List<MessageResponse> getSentMessages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        
        return messageRepository.findBySender(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer tous les messages non lus d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<MessageResponse> getUnreadMessages(Long userId) {
        return messageRepository.findUnreadByReceiverId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Compter le nombre de messages non lus
     */
    @Transactional(readOnly = true)
    public Long countUnreadMessages(Long userId) {
        return messageRepository.countUnreadByReceiverId(userId);
    }
    
    /**
     * Marquer un message comme lu
     */
    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message introuvable"));
        
        if (message.getIsRead()) {
            throw new RuntimeException("Le message est déjà marqué comme lu");
        }
        
        message.markAsRead();
        messageRepository.save(message);
    }
    
    /**
     * Récupérer la conversation entre deux utilisateurs
     */
    @Transactional(readOnly = true)
    public List<MessageResponse> getConversation(Long userId1, Long userId2) {
        // Vérifier que les deux utilisateurs existent
        userRepository.findById(userId1)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        userRepository.findById(userId2)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        
        return messageRepository.findConversationBetweenUsers(userId1, userId2).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Supprimer un message
     */
    public void deleteMessage(Long messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new RuntimeException("Message introuvable");
        }
        messageRepository.deleteById(messageId);
    }
    
    /**
     * Ajouter un tag à un message
     */
    public MessageResponse addTag(Long messageId, String tag) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message introuvable"));
        
        message.addTag(tag);
        Message updated = messageRepository.save(message);
        
        return mapToResponse(updated);
    }
    
    /**
     * Récupérer tous les messages d'un utilisateur (envoyés et reçus)
     */
    @Transactional(readOnly = true)
    public List<MessageResponse> getAllUserMessages(Long userId) {
        return messageRepository.findConversationByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    // ==================== MAPPER ====================
    
    private MessageResponse mapToResponse(Message message) {
        UserResponse sender = new UserResponse(
                message.getSender().getId(),
                message.getSender().getMatricule(),
                message.getSender().getPrenom(),
                message.getSender().getNom(),
                message.getSender().getEmail(),
                message.getSender().getTelephone(),
                message.getSender().getRole(),
                message.getSender().getIsActive()
        );

        UserResponse receiver = new UserResponse(
                message.getReceiver().getId(),
                message.getReceiver().getMatricule(),
                message.getReceiver().getPrenom(),
                message.getReceiver().getNom(),
                message.getReceiver().getEmail(),
                message.getReceiver().getTelephone(),
                message.getReceiver().getRole(),
                message.getReceiver().getIsActive()
        );
        
        return new MessageResponse(
                message.getId(),
                sender,
                receiver,
                message.getSubject(),
                message.getContent(),
                message.getIsRead(),
                message.getTags(),
                message.getSentAt()
        );
    }
}