package com.example.campusMaster.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.campusMaster.domain.entity.Message;
import com.example.campusMaster.domain.entity.User;

@Repository
public interface MessageRepository extends JpaRepository <Message, Long>{
    List<Message> findBySender(User sender);
    
    List<Message> findByReceiver(User receiver);
    
    @Query("SELECT m FROM Message m WHERE m.receiver.id = :userId AND m.isRead = false ORDER BY m.sentAt DESC")
    List<Message> findUnreadByReceiverId(Long userId);
    
    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId OR m.receiver.id = :userId) ORDER BY m.sentAt DESC")
    List<Message> findConversationByUserId(Long userId);
    
    @Query("SELECT m FROM Message m WHERE ((m.sender.id = :userId1 AND m.receiver.id = :userId2) OR (m.sender.id = :userId2 AND m.receiver.id = :userId1)) ORDER BY m.sentAt ASC")
    List<Message> findConversationBetweenUsers(Long userId1, Long userId2);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :userId AND m.isRead = false")
    Long countUnreadByReceiverId(Long userId);

}
