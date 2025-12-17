package com.example.campusMaster.domain.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table (name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nom obligatoire")
    @Column(nullable = false, unique = true, length = 200)
    private String name;
    
    @NotBlank(message = "Code obligatoire")
    @Column(nullable = false, unique = true, length = 20)
    private String code;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    // Relations
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private Set<CourseModule> modules = new HashSet<>();
    
    // Méthodes métier
    public void create() {
        // Logique de création
    }
    
    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public void getStatistics() {
        // Calcul des statistiques du département
    }
    
    // Méthodes utilitaires
    public int getModulesCount() {
        return modules.size();
    }
    
    public int getTotalCoursesCount() {
        return modules.stream()
            .mapToInt(CourseModule::getCoursesCount)
            .sum();
    }


}
