package com.kooyeoung.hrbank.dto.command.employee;

import com.kooyeoung.hrbank.entity.EmployeeStatus;

import java.time.LocalDate;

public record EmployeeUpdateCommand(
        String name,
        String email,
        String position,
        LocalDate hireDate,
        EmployeeStatus status
) {
}
