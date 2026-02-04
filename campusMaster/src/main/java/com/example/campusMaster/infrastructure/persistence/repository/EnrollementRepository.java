package com.example.campusMaster.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.campusMaster.domain.entity.CourseModule;
import com.example.campusMaster.domain.entity.Enrollment;
import com.example.campusMaster.domain.entity.User;

@Repository
public interface EnrollementRepository extends JpaRepository <Enrollment, Long>{
    List<Enrollment> findByUser(User user);
    
    List<Enrollment> findByModule(Module module);
    
    Optional<Enrollment> findByUserAndModule(User user, Module module);
    
    List<Enrollment> findByIsActive(Boolean isActive);
    
    @Query("SELECT e FROM Enrollment e WHERE e.user.id = :userId AND e.isActive = true")
    List<Enrollment> findActiveEnrollmentsByUserId(Long userId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.module.id = :moduleId AND e.isActive = true")
    List<Enrollment> findActiveEnrollmentsByModuleId(Long moduleId);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.module.id = :moduleId AND e.isActive = true")
    Long countActiveEnrollmentsByModuleId(Long moduleId);
    
    boolean existsByUserAndModule(User user, CourseModule module);

    List<Enrollment> findByUserId(Long userId);

    void deleteByUser(User user);
}
