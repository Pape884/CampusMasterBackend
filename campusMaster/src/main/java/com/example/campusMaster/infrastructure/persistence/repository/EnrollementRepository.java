package com.example.campusMaster.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.campusMaster.domain.entity.Course;
import com.example.campusMaster.domain.entity.Enrollment;
import com.example.campusMaster.domain.entity.User;

@Repository
public interface EnrollementRepository extends JpaRepository <Enrollment, Long>{
    List<Enrollment> findByStudent(User student);
    
    List<Enrollment> findByCourse(Course course);
    
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    
    List<Enrollment> findByIsActive(Boolean isActive);
    
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.isActive = true")
    List<Enrollment> findActiveEnrollmentsByStudentId(Long studentId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId AND e.isActive = true")
    List<Enrollment> findActiveEnrollmentsByCourseId(Long courseId);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.isActive = true")
    Long countActiveEnrollmentsByCourseId(Long courseId);
    
    boolean existsByStudentAndCourse(User student, Course course);

}
