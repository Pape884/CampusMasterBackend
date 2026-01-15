package com.example.campusMaster.application.Services;


import com.example.campusMaster.application.dto.request.CreateAssignmentRequest;
import com.example.campusMaster.application.dto.request.UpdateAssignmentRequest;
import com.example.campusMaster.application.dto.response.AssignmentResponse;
import com.example.campusMaster.domain.entity.Assignment;
import com.example.campusMaster.infrastructure.persistence.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    
    public AssignmentResponse createAssignment(CreateAssignmentRequest request) {
        Assignment assignment = Assignment.builder()
            .courseId(request.courseId())
            .description(request.description())
            .deadline(request.deadline())
            .build();
        
        Assignment saved = assignmentRepository.save(assignment);
        return mapToResponse(saved);
    }
    
    public AssignmentResponse updateAssignment(Long id, UpdateAssignmentRequest request) {
        Assignment assignment = assignmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Devoir introuvable"));
        
        if (request.description() != null) {
            assignment.setDescription(request.description());
        }
        if (request.deadline() != null) {
            assignment.setDeadline(request.deadline());
        }
        
        Assignment updated = assignmentRepository.save(assignment);
        return mapToResponse(updated);
    }
    
    public AssignmentResponse getAssignmentById(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Devoir introuvable"));
        return mapToResponse(assignment);
    }
    
    public List<AssignmentResponse> getAssignmentsByCourseId(Long courseId) {
        return assignmentRepository.findByCourseId(courseId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<AssignmentResponse> getUpcomingAssignments() {
        return assignmentRepository.findUpcomingAssignments(LocalDateTime.now()).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public void deleteAssignment(Long id) {
        assignmentRepository.deleteById(id);
    }
    
    // Mapper
    private AssignmentResponse mapToResponse(Assignment assignment) {
        return new AssignmentResponse(
            assignment.getId(),
            assignment.getCourseId(),
            assignment.getDescription(),
            assignment.getDeadline(),
            assignment.getSubmissions().size(),
            assignment.getCreatedAt()
        );
    }

}
