package com.kooyeoung.hrbank.dto.repository.employeeHistory;

import java.time.LocalDateTime;

public record EmployeeHistoryEditCountCondition(
        LocalDateTime fromDate,
        LocalDateTime toDate
) {
}
