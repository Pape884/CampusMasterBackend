package com.example.campusMaster.application.Services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.campusMaster.application.dto.request.users.CreateUserRequest;
import com.example.campusMaster.application.dto.request.users.UpdateUserRequest;
import com.example.campusMaster.application.dto.response.enrollement.EnrollmentResponse;
import com.example.campusMaster.application.dto.response.modules.ModuleResponse;
import com.example.campusMaster.application.dto.response.users.UserResponse;
import com.example.campusMaster.application.dto.response.users.UserStats;
import com.example.campusMaster.domain.entity.CourseModule;
import com.example.campusMaster.domain.entity.Enrollment;
import com.example.campusMaster.domain.entity.User;
import com.example.campusMaster.domain.enums.Role;
import com.example.campusMaster.infrastructure.exception.ResourceAlreadyExistsException;
import com.example.campusMaster.infrastructure.exception.ResourceNotFoundException;
import com.example.campusMaster.infrastructure.persistence.repository.DepartmentRepository;
import com.example.campusMaster.infrastructure.persistence.repository.EnrollementRepository;
import com.example.campusMaster.infrastructure.persistence.repository.ModuleRepository;
import com.example.campusMaster.infrastructure.persistence.repository.UserRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;
    private final DepartmentRepository departmentRepository;
    private final EnrollementRepository enrollmentRepository;
    private final PasswordEncoder passwordEncoder;

    // Méthode avec pagination et filtres
    public Page<User> getUsers(String search, String role, Boolean isActive,
            int page, int limit, String sortBy, String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        int pageIndex = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageIndex, limit, sort);

        Specification<User> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Recherche par matricule, nom, prénom ou email
            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("matricule")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("nom")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("prenom")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchPattern));
                predicate = criteriaBuilder.and(predicate, searchPredicate);
            }

            // Filtre par rôle
            if (role != null && !role.isEmpty()) {
                try {
                    Role roleEnum = Role.valueOf(role.toUpperCase());
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.equal(root.get("role"), roleEnum));
                } catch (IllegalArgumentException e) {
                    // Ignorer si le rôle est invalide
                }
            }

            // Filtre par statut actif/inactif
            if (isActive != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("isActive"), isActive));
            }

            return predicate;
        };

        return userRepository.findAll(spec, pageable);
    }

    // Statistiques des utilisateurs
    public UserStats getUserStats() {
        long total = userRepository.count();
        long active = userRepository.countByIsActive(true);
        long inactive = userRepository.countByIsActive(false);

        long students = userRepository.countByRole(Role.STUDENT);
        long teachers = userRepository.countByRole(Role.TEACHER);
        long admins = userRepository.countByRole(Role.ADMIN);

        return UserStats.builder()
                .totalUsers(total)
                .activeUsers(active)
                .inactiveUsers(inactive)
                .studentsCount(students)
                .teachersCount(teachers)
                .adminsCount(admins)
                .build();
    }

    // Recherche simple
    public List<UserResponse> searchUsers(String query) {
        String searchPattern = "%" + query.toLowerCase() + "%";

        Specification<User> spec = (root, q, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("matricule")), searchPattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nom")), searchPattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("prenom")), searchPattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchPattern));

        return userRepository.findAll(spec).stream()
                .map(this::mapAllUsersResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Un utilisateur avec cet email existe déjà");
        }

        User user = User.builder()
                .matricule(request.getMatricule())
                .prenom(request.getPrenom())
                .nom(request.getNom())
                .email(request.getEmail())
                .telephone(request.getTelephone())
                .role(request.getRole())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .build();

        // 1️⃣ Sauvegarder le user
        User savedUser = userRepository.save(user);

        // 2️⃣ Créer les enrollments
        if (request.getModules() != null && !request.getModules().isEmpty()) {

            List<Enrollment> enrollments = request.getModules().stream()
                    .map(moduleId -> {
                        CourseModule module = moduleRepository.findById(Long.valueOf(moduleId))
                                .orElseThrow(() -> new ResourceNotFoundException("Module introuvable : " + moduleId));

                        Enrollment enrollment = Enrollment.builder()
                                .user(savedUser)
                                .module(module)
                                .isActive(true)
                                .enrolledAt(LocalDateTime.now())
                                .build();

                        return enrollment;
                    })
                    .toList();

            // 3️⃣ Lier côté user
            savedUser.getEnrollments().addAll(enrollments);

            // 4️⃣ Sauvegarder les enrollments
            enrollmentRepository.saveAll(enrollments);
        }

        return mapToResponse(savedUser);
    }

    // Mettre à jour un utilisateur
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        // 🔐 Email unique
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Cet email est déjà utilisé");
        }

        // 🔄 Infos simples
        user.setPrenom(request.getPrenom());
        user.setNom(request.getNom());
        user.setEmail(request.getEmail());
        user.setTelephone(request.getTelephone());

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        /*
         * =========================
         * 🎓 GESTION DES ENROLLMENTS
         * =========================
         */
        if (request.getModuleIds() != null) {

            // 1️⃣ Supprimer les anciens enrollments
            enrollmentRepository.deleteByUser(user);

            // 2️⃣ Ajouter les nouveaux
            List<Enrollment> newEnrollments = request.getModuleIds().stream()
                    .map(moduleId -> {
                        CourseModule module = moduleRepository.findById(moduleId)
                                .orElseThrow(() -> new ResourceNotFoundException("Module introuvable: " + moduleId));

                        Enrollment enrollment = new Enrollment();
                        enrollment.setUser(user);
                        enrollment.setModule(module);
                        enrollment.setIsActive(true);
                        enrollment.setEnrolledAt(LocalDateTime.now());
                        return enrollment;
                    })
                    .toList();

            enrollmentRepository.saveAll(newEnrollments);

            // 3️⃣ Mettre à jour la relation côté user
            user.setEnrollments(newEnrollments);
        }

        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }

    // Mettre à jour le statut
    public UserResponse updateStatus(Long id, boolean status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        user.setIsActive(status);
        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        userRepository.delete(user);
    }

    // Méthodes existantes
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return mapToResponse(user);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return mapToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse updateProfile(Long userId, String prenom, String nom) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        user.setPrenom(prenom);
        user.setNom(nom);
        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        user.setIsActive(true);
        userRepository.save(user);
    }

    public Long countUsersByRole(Role role) {
        return userRepository.countByRole(role);
    }

    // Mapper

    private ModuleResponse mapModule(CourseModule module) {
        return ModuleResponse.builder()
                .id(module.getId())
                .code(module.getCode())
                .name(module.getName())
                .semestre(module.getSemestre())
                .departmentId(module.getDepartment().getId())
                .coursesCount(module.getCourses().size())
                .build();
    }

    private EnrollmentResponse mapEnrollment(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .module(mapModule(enrollment.getModule()))
                .enrolledAt(enrollment.getEnrolledAt())
                .isActive(enrollment.getIsActive())
                .finalGrade(enrollment.getFinalGrade())
                .build();

    }

    private UserResponse mapToResponse(User user) {

        List<EnrollmentResponse> enrollments = user.getEnrollments()
                .stream()
                .map(this::mapEnrollment)
                .toList();

        return UserResponse.builder()
                .id(user.getId())
                .matricule(user.getMatricule())
                .prenom(user.getPrenom())
                .nom(user.getNom())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .enrollments(enrollments)
                .build();
    }

    private UserResponse mapAllUsersResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .matricule(user.getMatricule())
                .prenom(user.getPrenom())
                .nom(user.getNom())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build();
    }
}