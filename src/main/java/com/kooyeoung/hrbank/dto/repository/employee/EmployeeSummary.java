package com.kooyeoung.hrbank.dto.repository.employee;

import com.kooyeoung.hrbank.entity.snapshot.EmployeeSnapshot;

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
        String status,
        Long profileImageId
) {
}
