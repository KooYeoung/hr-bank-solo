package com.kooyeoung.hrbank.dto.repository.employee;

import java.time.LocalDate;

public record EmployeeTrendCondition(
        LocalDate from,
        LocalDate to,
        String unit
) {
}
