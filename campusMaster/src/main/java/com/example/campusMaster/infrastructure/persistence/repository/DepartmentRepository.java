package com.example.campusMaster.infrastructure.persistence.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.campusMaster.domain.entity.Department;

public interface DepartmentRepository
        extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {
            boolean existsByCode(String code);

    Long countByIsActive(Boolean isActive);

    @Query("""
                SELECT COUNT(u)
                FROM User u
                WHERE u.department.id = :departmentId AND u.role = 'STUDENT'
            """)
    long countStudents(@Param("departmentId") Long departmentId);

    @Query("""
                SELECT COUNT(u)
                FROM User u
                WHERE u.department.id = :departmentId AND u.role = 'TEACHER'
            """)
    long countTeachers(@Param("departmentId") Long departmentId);

    @Query("""
                SELECT COUNT(m)
                FROM CourseModule m
                WHERE m.department.id =:departmentId
            """)
    long countModules(@Param("departmentId") Long departmentId);

    @Query("""
                SELECT COUNT(m)
                FROM CourseModule m
            """)
    long countAllModules();

    @Query("""
        SELECT DISTINCT d
        FROM Department d
        LEFT JOIN FETCH d.modules m
        LEFT JOIN FETCH m.courses
        WHERE d.id = :id
    """)
    Optional<Department> findByIdWithModulesAndCourses(@Param("id") Long id);
}
