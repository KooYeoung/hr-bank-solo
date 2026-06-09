package com.kooyeoung.hrbank.dto.response;

import com.kooyeoung.hrbank.dto.repository.employeeHistory.EmployeeHistorySummary;

import java.time.LocalDateTime;

public record ChangeLogDto(
        Long id
        ,String type
        ,String employeeNumber
        ,String memo
        ,String ipAddress
        ,LocalDateTime at
) {

    public static ChangeLogDto from(EmployeeHistorySummary summary){
        return new ChangeLogDto(
                summary.id()
                , summary.type()
                , summary.employeeNumber()
                , summary.memo()
                ,summary.ipAddress()
                ,summary.at()
        );
    }
}
