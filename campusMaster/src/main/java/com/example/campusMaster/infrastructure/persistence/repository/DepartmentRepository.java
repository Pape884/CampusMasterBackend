package com.example.campusMaster.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.campusMaster.domain.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository <Department, Long> {
    Optional<Department> findByCode(String code);
    
    Optional<Department> findByName(String name);
    
    boolean existsByCode(String code);
    
    boolean existsByName(String name);

}
