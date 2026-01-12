package com.example.campusMaster.infrastructure.persistence.repository;
import com.example.campusMaster.domain.entity.Assignment;
import com.example.campusMaster.domain.entity.Submission;
import com.example.campusMaster.domain.entity.User;
import com.example.campusMaster.domain.enums.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository <Submission, Long> {
    List<Submission> findByAssignment(Assignment assignment);
    
    List<Submission> findByStudent(User student);
    
    Optional<Submission> findByAssignmentAndStudent(Assignment assignment, User student);
    
    List<Submission> findBySubmissionStatus(SubmissionStatus status);
    
    @Query("SELECT s FROM Submission s WHERE s.assignment.id = :assignmentId")
    List<Submission> findByAssignmentId(Long assignmentId);
    
    @Query("SELECT s FROM Submission s WHERE s.student.id = :studentId")
    List<Submission> findByStudentId(Long studentId);
    
    @Query("SELECT s FROM Submission s WHERE s.assignment.id = :assignmentId AND s.student.id = :studentId")
    Optional<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
    
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assignment.id = :assignmentId AND s.submissionStatus = 'SUBMITTED'")
    Long countSubmittedByAssignmentId(Long assignmentId);
    
    @Query("SELECT s FROM Submission s WHERE s.submissionStatus = 'SUBMITTED' AND s.grade IS NULL")
    List<Submission> findUngraded();

}
