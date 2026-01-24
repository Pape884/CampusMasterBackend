package com.example.campusMaster.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.campusMaster.domain.entity.Grade;
import com.example.campusMaster.domain.entity.Submission;
import com.example.campusMaster.domain.entity.User;

@Repository
public interface GradeRepository extends JpaRepository <Grade, Long> {
    Optional<Grade> findBySubmission(Submission submission);
    
    List<Grade> findByGradedBy(User teacher);
    
    @Query("SELECT g FROM Grade g WHERE g.submission.student.id = :studentId")
    List<Grade> findByStudentId(Long studentId);
    
    @Query("SELECT g FROM Grade g WHERE g.submission.assignment.course.id = :courseId")
    List<Grade> findByCourseId(Long courseId);
    
    @Query("SELECT AVG(g.points) FROM Grade g WHERE g.submission.assignment.id = :assignmentId")
    Double getAverageGradeByAssignmentId(Long assignmentId);
    
    @Query("SELECT AVG(g.points) FROM Grade g WHERE g.submission.student.id = :studentId")
    Double getAverageGradeByStudentId(Long studentId);

}
