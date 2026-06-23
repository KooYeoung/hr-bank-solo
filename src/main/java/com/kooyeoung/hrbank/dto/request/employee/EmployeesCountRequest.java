package com.kooyeoung.hrbank.dto.request.employee;

import com.kooyeoung.hrbank.dto.repository.employee.EmployeesCountCondition;
import com.kooyeoung.hrbank.entity.EmployeeStatus;

import java.time.LocalDate;

public record EmployeesCountRequest(
        String status,
        LocalDate fromDate,
        LocalDate toDate
) {

    public EmployeeStatus getStatusOrDefault() {
        if (status == null || status.isBlank()) return null;

        return EmployeeStatus.from(status);
    }

    public LocalDate getToDateOrDefault() {
        if (toDate == null || fromDate == null) return LocalDate.now();
        return toDate;
    }

    public EmployeesCountCondition toCondition() {
        return new EmployeesCountCondition(
                getStatusOrDefault(),
                fromDate,
                getToDateOrDefault()
        );
    }
}
