package com.example.campusMaster.application.dto.response.departments;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentStats {
    
    private long totalDepartments;
    private long activeDepartments;
    private long inactiveDepartments;
    private long totalModules;
}
