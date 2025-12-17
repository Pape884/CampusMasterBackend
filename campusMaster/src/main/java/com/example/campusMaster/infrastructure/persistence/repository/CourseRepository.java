package com.example.campusMaster.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.campusMaster.domain.entity.Course;
import com.example.campusMaster.domain.entity.User;

@Repository
public interface CourseRepository extends JpaRepository <Course , Long> {
    Optional<Course> findByCode(String code);
    
    List<Course> findByTeacher(User teacher);
    
    List<Course> findByIsActive(Boolean isActive);
    
    List<Course> findBySemestre(String semestre);
    
    List<Course> findByAnnee(Integer annee);
    
    @Query("SELECT c FROM Course c WHERE c.teacher.id = :teacherId AND c.isActive = true")
    List<Course> findActiveCoursesbyTeacher(Long teacherId);
    
    @Query("SELECT c FROM Course c WHERE c.semestre = :semestre AND c.annee = :annee")
    List<Course> findBySestrAndAnnee(String semestre, Integer annee);
    
    boolean existsByCode(String code);

}
