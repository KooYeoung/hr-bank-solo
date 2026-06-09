package com.kooyeoung.hrbank.dto.repository.employeeHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmployeeHistorySearchCondition(
        String employeeNumber
        , String type
        , String memo
        , String ipAddress
        , LocalDateTime atFrom
        , LocalDateTime atTo
        , String sortField
        , String cursor
        , long idAfter
        , boolean hasCursor
        , boolean isDesc
        , int size
) {
}
