package com.example.campusMaster.infrastructure.persistence.repository;

import com.example.campusMaster.domain.entity.Assignment;
import com.example.campusMaster.domain.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface AssignmentRepository extends JpaRepository <Assignment, Long> {
    List<Assignment> findByCourse(Course course);
    
    @Query("SELECT a FROM Assignment a WHERE a.course.id = :courseId")
    List<Assignment> findByCourseId(Long courseId);
    
    @Query("SELECT a FROM Assignment a WHERE a.deadline BETWEEN :start AND :end")
    List<Assignment> findByDeadlineBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT a FROM Assignment a WHERE a.deadline > :now ORDER BY a.deadline ASC")
    List<Assignment> findUpcomingAssignments(LocalDateTime now);
    
    @Query("SELECT a FROM Assignment a WHERE a.course.id = :courseId AND a.deadline > :now")
    List<Assignment> findActiveByCourseId(Long courseId, LocalDateTime now);

}
