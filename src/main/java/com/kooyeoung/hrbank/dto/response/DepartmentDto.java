package com.kooyeoung.hrbank.dto.response;

import com.kooyeoung.hrbank.dto.repository.DepartmentSummary;
import com.kooyeoung.hrbank.entity.Department;

public record DepartmentDto(
        Long id
        ,String name
        , String description
        , String establishedDate
        , Long employeeCount
) {

    public static DepartmentDto from(Department department, Long employeeCount) {
        return new DepartmentDto(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.getEstablishedDate().toString(),
                employeeCount
        );

    }

    public static DepartmentDto from(DepartmentSummary summary){
        return new DepartmentDto(
                summary.id()
                ,summary.name()
                ,summary.description()
                ,summary.establishedDate().toString()
                ,summary.employeeCount()
        );
    }
}
