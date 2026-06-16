package com.kooyeoung.hrbank.dto.request.employee;

import com.kooyeoung.hrbank.dto.repository.employee.EmployeesCountCondition;
import com.kooyeoung.hrbank.entity.EmployeeStatus;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public record EmployeesCountRequest(
        String status,
        LocalDate fromDate,
        LocalDate toDate
) {

    private final static Set<String> AVAILABLE_STATUS = Arrays.stream(EmployeeStatus.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    public String getStatusOrDefault(){
        if (status == null || status.isBlank()) return "";

        String normalizedStatus = status.toUpperCase();
        return AVAILABLE_STATUS.contains(normalizedStatus) ? EmployeeStatus.valueOf(normalizedStatus).toString() : "";
    }

    public LocalDate getToDateOrDefault(){
        if(toDate == null || fromDate == null) return LocalDate.now();
        return toDate;
    }

    public static EmployeesCountCondition from(EmployeesCountRequest request){
        return new EmployeesCountCondition(
                request.getStatusOrDefault(),
                request.fromDate,
                request.getToDateOrDefault()
        );
    }
}
