package com.example.campusMaster.infrastructure.persistence.repository;

import com.example.campusMaster.domain.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    
    List<Assignment> findByCourse_Id(Long courseId);
    
    @Query("SELECT a FROM Assignment a WHERE a.deadline > :now ORDER BY a.deadline ASC")
    List<Assignment> findUpcomingAssignments(@Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Assignment a WHERE a.course.id = :courseId AND a.deadline BETWEEN :start AND :end")
    List<Assignment> findByCourseAndDeadlineBetween(
            @Param("courseId") Long courseId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}