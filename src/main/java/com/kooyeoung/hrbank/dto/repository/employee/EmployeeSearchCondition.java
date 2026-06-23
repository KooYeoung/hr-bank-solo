package com.kooyeoung.hrbank.dto.repository.employee;

import com.kooyeoung.hrbank.entity.EmployeeStatus;

import java.time.LocalDate;

public record EmployeeSearchCondition(
        String nameOrEmail,
        String employeeNumber,
        String departmentName,
        String position,
        LocalDate hireDateFrom,
        LocalDate hireDateTo,
        EmployeeStatus status,
        String sortField,
        String cursor,
        Long idAfter,
        Boolean hasCursor,
        Boolean isDesc,
        Integer size
) {

}
