package com.kooyeoung.hrbank.dto.repository;

import java.time.LocalDate;

public record DepartmentSummary(
        Long id,
        String name,
        String description,
        LocalDate establishedDate,
        Long employeeCount
) {
}
