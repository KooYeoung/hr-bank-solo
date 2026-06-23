package com.kooyeoung.hrbank.dto.command.employee;

import java.time.LocalDate;

public record EmployeeCreateCommand(
        String name,
        String email,
        String position,
        LocalDate hireDate
) {
}
