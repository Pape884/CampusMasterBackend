package com.example.campusMaster.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.campusMaster.domain.entity.Course;
import com.example.campusMaster.domain.entity.User;

@Repository
public interface CourseRepository extends JpaRepository <Course , Long> {
    Optional<Course> findByCode(String code);
    
    List<Course> findByTeacher(User teacher);
    
    List<Course> findByIsActive(Boolean isActive);

    List<Course> findByModuleId(Long moduleId); 
    
    boolean existsByCode(String code);

}
