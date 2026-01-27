package com.example.campusMaster.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.campusMaster.domain.enums.Role;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = true, length = 10)
    private String matricule;

    @NotBlank(message = "Prénom obligatoire")
    @Column(nullable = false, length = 50)
    private String prenom;

    @NotBlank(message = "Nom obligatoire")
    @Column(nullable = false, length = 50)
    private String nom;

    @Email(message = "Email invalide")
    @NotBlank(message = "Email obligatoire")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(unique = true, nullable = true)
    private Long telephone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @NotBlank(message = "Mot de passe obligatoire")
    @Size(min = 8, message = "Mot de passe minimum 8 caractères")
    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Course> taughtCourses = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Enrollment> enrollments = new HashSet<>();
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @Builder.Default    
    private Set<Submission> submissions = new HashSet<>();
    
    @OneToMany(mappedBy = "gradedBy", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Grade> grades = new HashSet<>();
    
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Message> sentMessages = new HashSet<>();
    
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Message> receivedMessages = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Notification> notifications = new HashSet<>();
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Announcement> announcements = new HashSet<>();
}