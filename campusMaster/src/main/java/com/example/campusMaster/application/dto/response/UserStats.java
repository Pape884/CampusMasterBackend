package com.example.campusMaster.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStats {
     private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;
    private long studentsCount;
    private long teachersCount;
    private long adminsCount;

}
