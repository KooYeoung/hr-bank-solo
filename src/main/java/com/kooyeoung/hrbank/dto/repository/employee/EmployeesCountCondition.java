package com.kooyeoung.hrbank.dto.repository.employee;

import com.kooyeoung.hrbank.entity.EmployeeStatus;

import java.time.LocalDate;

public record EmployeesCountCondition(
        EmployeeStatus status,
        LocalDate fromDate,
        LocalDate toDate

) {
}
