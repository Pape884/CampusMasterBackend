package com.example.campusMaster.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.campusMaster.domain.entity.Notification;
import com.example.campusMaster.domain.entity.User;
import com.example.campusMaster.domain.enums.NotificationType;

@Repository
public interface NotificationRepository  extends JpaRepository <Notification, Long>{
    List<Notification> findByUser(User user);
    
    List<Notification> findByNotificationType(NotificationType type);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUserId(Long userId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    Long countUnreadByUserId(Long userId);
    
    void deleteByUserAndIsRead(User user, Boolean isRead);

}
