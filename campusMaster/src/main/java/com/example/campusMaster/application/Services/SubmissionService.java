package com.example.campusMaster.application.Services;

import com.example.campusMaster.application.dto.response.submissions.SubmissionResponse;
import com.example.campusMaster.application.dto.response.users.UserResponse;
import com.example.campusMaster.domain.entity.Assignment;
import com.example.campusMaster.domain.entity.Submission;
import com.example.campusMaster.domain.entity.User;
import com.example.campusMaster.domain.enums.SubmissionStatus;
import com.example.campusMaster.infrastructure.exception.ResourceAlreadyExistsException;
import com.example.campusMaster.infrastructure.exception.ResourceNotFoundException;
import com.example.campusMaster.infrastructure.persistence.repository.AssignmentRepository;
import com.example.campusMaster.infrastructure.persistence.repository.SubmissionRepository;
import com.example.campusMaster.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubmissionService {
    
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    
    /**
     * Soumettre un devoir
     */
    public SubmissionResponse submitAssignment(Long assignmentId, Long studentId, 
                                               MultipartFile file, String comments) {
        log.info("Soumission du devoir {} par l'étudiant {}", assignmentId, studentId);
        
        // Récupérer le devoir
        Assignment assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Devoir introuvable"));
        
        // Récupérer l'étudiant
        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable"));
        
        // Vérifier si l'étudiant a déjà soumis ce devoir
        submissionRepository.findByAssignmentAndStudent(assignment, student)
            .ifPresent(existing -> {
                throw new ResourceAlreadyExistsException("Vous avez déjà soumis ce devoir. Utilisez la mise à jour pour modifier votre soumission.");
            });
        
        // Vérifier la deadline
        if (LocalDateTime.now().isAfter(assignment.getDeadline())) {
            log.warn("Soumission tardive pour le devoir {} par l'étudiant {}", assignmentId, studentId);
        }
        
        // Upload du fichier
        String fileUrl = fileStorageService.uploadFile(file, "submissions");
        
        // Créer la soumission
        Submission submission = Submission.builder()
            .assignment(assignment)
            .student(student)
            .fileUrl(fileUrl)
            .fileName(file.getOriginalFilename())
            .comments(comments)
            .uploadedAt(LocalDateTime.now())
            .submittedAt(LocalDateTime.now())
            .version(1)
            .submissionStatus(SubmissionStatus.SUBMITTED)
            .build();
        
        submission.submit();
        
        Submission savedSubmission = submissionRepository.save(submission);
        log.info("Devoir soumis avec succès - ID: {}", savedSubmission.getId());
        
        return mapToResponse(savedSubmission);
    }
    
    /**
     * Mettre à jour une soumission (nouvelle version)
     */
    public SubmissionResponse updateSubmission(Long submissionId, MultipartFile file, String comments) {
        log.info("Mise à jour de la soumission {}", submissionId);
        
        Submission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Soumission introuvable"));
        
        // Vérifier que la deadline n'est pas dépassée
        if (LocalDateTime.now().isAfter(submission.getAssignment().getDeadline())) {
            throw new ResourceNotFoundException("Impossible de mettre à jour : la deadline est dépassée");
        }
        
        // Supprimer l'ancien fichier
        try {
            fileStorageService.deleteFile(submission.getFileUrl());
        } catch (Exception e) {
            log.warn("Erreur lors de la suppression de l'ancien fichier: {}", e.getMessage());
        }
        
        // Upload du nouveau fichier
        String newFileUrl = fileStorageService.uploadFile(file, "submissions");
        
        // Mettre à jour la soumission
        submission.setFileUrl(newFileUrl);
        submission.setFileName(file.getOriginalFilename());
        submission.setComments(comments);
        submission.setUploadedAt(LocalDateTime.now());
        submission.updateVersion();
        
        Submission updatedSubmission = submissionRepository.save(submission);
        log.info("Soumission mise à jour - Nouvelle version: {}", updatedSubmission.getVersion());
        
        return mapToResponse(updatedSubmission);
    }
    
    /**
     * Récupérer une soumission par ID
     */
    @Transactional(readOnly = true)
    public SubmissionResponse getSubmissionById(Long id) {
        log.debug("Récupération de la soumission {}", id);
        
        Submission submission = submissionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Soumission introuvable"));
        
        return mapToResponse(submission);
    }
    
    /**
     * Lister toutes les soumissions d'un devoir
     */
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId) {
        log.debug("Récupération des soumissions pour le devoir {}", assignmentId);
        
        if (!assignmentRepository.existsById(assignmentId)) {
            throw new ResourceNotFoundException("Devoir introuvable");
        }
        
        List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);
        
        return submissions.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Lister toutes les soumissions d'un étudiant
     */
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsByStudent(Long studentId) {
        log.debug("Récupération des soumissions de l'étudiant {}", studentId);
        
        if (!userRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Étudiant introuvable");
        }
        
        List<Submission> submissions = submissionRepository.findByStudentId(studentId);
        
        return submissions.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Retirer une soumission (passer en DRAFT)
     */
    public void withdrawSubmission(Long id) {
        log.info("Retrait de la soumission {}", id);
        
        Submission submission = submissionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Soumission introuvable"));
        
        // Vérifier que la deadline n'est pas dépassée
        if (LocalDateTime.now().isAfter(submission.getAssignment().getDeadline())) {
            throw new ResourceNotFoundException("Impossible de retirer : la deadline est dépassée");
        }
        
        submission.withdraw();
        submissionRepository.save(submission);
        
        log.info("Soumission retirée avec succès - ID: {}", id);
    }
    
    /**
     * Vérifier si un étudiant a soumis un devoir
     */
    @Transactional(readOnly = true)
    public boolean hasStudentSubmitted(Long assignmentId, Long studentId) {
        return submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
            .filter(s -> s.getSubmissionStatus() == SubmissionStatus.SUBMITTED)
            .isPresent();
    }
    
    /**
     * Compter les soumissions soumises d'un devoir
     */
    @Transactional(readOnly = true)
    public long countSubmittedByAssignment(Long assignmentId) {
        Long count = submissionRepository.countSubmittedByAssignmentId(assignmentId);
        return count != null ? count : 0L;
    }
    
    /**
     * Récupérer les soumissions non notées
     */
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getUngradedSubmissions() {
        log.debug("Récupération des soumissions non notées");
        
        List<Submission> submissions = submissionRepository.findUngraded();
        
        return submissions.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Mapper l'entité vers le DTO
     */
    private SubmissionResponse mapToResponse(Submission submission) {
        User student = submission.getStudent();
        
        UserResponse studentResponse = new UserResponse(
            student.getId(),
            student.getMatricule(),
            student.getPrenom(),
            student.getNom(),
            student.getEmail(),
            student.getTelephone(),
            student.getRole(),
            student.getIsActive()
        );
        
        return new SubmissionResponse(
            submission.getId(),
            submission.getFileUrl(),
            submission.getFileName(),
            submission.getVersion(),
            submission.getSubmissionStatus(),
            submission.getComments(),
            submission.getSubmittedAt(),
            submission.getUploadedAt(),
            studentResponse
        );
    }
}