package com.example.campusMaster.domain.entity;

import java.util.HashSet;
import java.util.Set;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "module")

public class CourseModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Code module obligatoire")
    @Column(unique = true, nullable = false, length = 20)
    private String code;
    
    @NotBlank(message = "Nom obligatoire")
    @Column(nullable = false, length = 20)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, length = 20)
    private String semestre;
    
    // Relations

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Course> courses = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id") 
    private Department department;

    
    // Méthodes métier
    public void create() {
        // Logique de création
    }
    
    public void update(String name,String code, String description) {
        this.name = name;
        this.code = code;
        this.description = description;
    }
    
    public void assignCourses(Set<Course> courses) {
        this.courses = courses;
    }
    
    // Méthodes utilitaires
    public int getCoursesCount() {
        return courses.size();
    }

}
