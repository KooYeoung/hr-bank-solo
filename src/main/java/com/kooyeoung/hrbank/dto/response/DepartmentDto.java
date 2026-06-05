package com.kooyeoung.hrbank.dto.response;

import com.kooyeoung.hrbank.dto.repository.department.DepartmentSummary;
import com.kooyeoung.hrbank.entity.Department;

import java.time.LocalDate;

public record DepartmentDto(
        Long id
        , String name
        , String description
        , LocalDate establishedDate
        , Long employeeCount
) {

    public static DepartmentDto from(Department department, Long employeeCount) {
        return new DepartmentDto(
                department.getId()
                , department.getName()
                , department.getDescription()
                , department.getEstablishedDate()
                , employeeCount
        );

    }

    public static DepartmentDto from(DepartmentSummary summary){
        return new DepartmentDto(
                summary.id()
                ,summary.name()
                ,summary.description()
                ,summary.establishedDate()
                ,summary.employeeCount()
        );
    }
}
