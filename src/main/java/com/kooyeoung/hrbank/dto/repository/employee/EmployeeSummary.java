package com.kooyeoung.hrbank.dto.repository.employee;

import com.kooyeoung.hrbank.entity.EmployeeStatus;

import java.time.LocalDate;

public record EmployeeSummary(
        Long id,
        String name,
        String email,
        String employeeNumber,
        Long departmentId,
        String departmentName,
        String position,
        LocalDate hireDate,
        EmployeeStatus status,
        Long profileImageId
) {
}
