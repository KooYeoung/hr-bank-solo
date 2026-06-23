package com.kooyeoung.hrbank.dto.repository.employeeHistory;

import java.time.LocalDateTime;

public record EmployeeHistorySummary(
        Long id,
        String type,
        String employeeNumber,
        String memo,
        String ipAddress,
        LocalDateTime at
) {
}
