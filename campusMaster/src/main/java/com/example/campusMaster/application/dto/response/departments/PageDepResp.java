package com.example.campusMaster.application.dto.response.departments;

import java.util.List;

import com.example.campusMaster.application.dto.response.Pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageDepResp<T> {

    private List<T> data;
    private Pagination pagination;
    private DepartmentStats stats;
}

