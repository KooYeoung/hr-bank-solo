package com.kooyeoung.hrbank.dto.repository.employeeHistory;

import com.kooyeoung.hrbank.entity.HistoryType;

import java.time.LocalDateTime;

public record EmployeeHistorySearchCondition(
        String employeeNumber,
        HistoryType type,
        String memo,
        String ipAddress,
        LocalDateTime atFrom,
        LocalDateTime atTo,
        String sortField,
        String cursor,
        long idAfter,
        boolean hasCursor,
        boolean isDesc,
        int size
) {
}
