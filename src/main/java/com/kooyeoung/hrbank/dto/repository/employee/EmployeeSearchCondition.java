package com.kooyeoung.hrbank.dto.repository.employee;

import com.kooyeoung.hrbank.entity.EmployeeStatus;

import java.time.LocalDate;

public record EmployeeSearchCondition(
        String nameOrEmail
        , String employeeNumber
        , String departmentName
        , String position
        , LocalDate hireDateFrom
        , LocalDate hireDateTo
        , String status
        , String sortField
        , String cursor
        , long idAfter
        , boolean hasCursor
        , boolean isDesc
        , int size
) {

}
