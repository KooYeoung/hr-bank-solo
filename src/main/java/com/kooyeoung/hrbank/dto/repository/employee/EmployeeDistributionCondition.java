package com.kooyeoung.hrbank.dto.repository.employee;

import com.kooyeoung.hrbank.entity.EmployeeStatus;

public record EmployeeDistributionCondition(
        String groupBy,
        EmployeeStatus status
) {
}
