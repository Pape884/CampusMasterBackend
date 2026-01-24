package com.example.campusMaster.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.campusMaster.domain.entity.CourseModule;
import com.example.campusMaster.domain.entity.Department;
@Repository
public interface ModuleRepository extends JpaRepository <CourseModule, Long>{

    Optional<Module> findByCode(String code);
    List<CourseModule> findByDepartmentId(Long departmentId);
    List<Module> findByDepartment(Department department);
    
    List<Module> findBySemestre(Integer semestre);
    
    boolean existsByCode(String code);

}
