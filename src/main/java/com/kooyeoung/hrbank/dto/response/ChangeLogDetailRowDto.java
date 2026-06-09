package com.kooyeoung.hrbank.dto.response;

import com.kooyeoung.hrbank.entity.EmployeeProperties;
import com.kooyeoung.hrbank.entity.HistoryType;

import java.time.LocalDateTime;

public record ChangeLogDetailRowDto(
        Long id,
        HistoryType type,
        String employeeNumber,
        String memo,
        String ipAddress,
        LocalDateTime at,
        String employeeName,
        Long profileImageId,
        EmployeeProperties property,
        String beforeValue,
        String afterValue
) {
}
