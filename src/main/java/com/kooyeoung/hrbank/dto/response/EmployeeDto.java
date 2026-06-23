package com.kooyeoung.hrbank.dto.response;

import com.kooyeoung.hrbank.dto.repository.employee.EmployeeSummary;
import com.kooyeoung.hrbank.entity.snapshot.EmployeeSnapshot;

import java.time.LocalDate;

public record EmployeeDto(
        Long id,
        String name,
        String email,
        String employeeNumber,
        Long departmentId,
        String departmentName,
        String position,
        LocalDate hireDate,
        String status,
        Long profileImageId
) {
    public static EmployeeDto from(EmployeeSnapshot snapshot) {
        return new EmployeeDto(
                snapshot.id(),
                snapshot.name(),
                snapshot.email(),
                snapshot.employeeNumber(),
                snapshot.departmentId(),
                snapshot.departmentName(),
                snapshot.position(),
                snapshot.hireDate(),
                snapshot.status().name(),
                snapshot.profileImageId()
        );
    }

    public static EmployeeDto from(EmployeeSummary summary){
        return new EmployeeDto(
                summary.id()
                ,summary.name()
                ,summary.email()
                ,summary.employeeNumber()
                ,summary.departmentId()
                , summary.departmentName()
                ,summary.position()
                ,summary.hireDate()
                ,summary.status().name()
                ,summary.profileImageId()
        );
    }
}
