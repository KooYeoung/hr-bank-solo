package com.kooyeoung.hrbank.dto.repository.employee;

import java.time.LocalDate;

public record EmployeesCountCondition(
        String status,
        LocalDate fromDate,
        LocalDate toDate

) {
}
