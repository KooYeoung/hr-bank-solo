package com.kooyeoung.hrbank.entity.snapshot;

import com.kooyeoung.hrbank.entity.EmployeeStatus;

import java.time.LocalDate;

public record EmployeeSnapshot(Long id,
                               String name,
                               String email,
                               String employeeNumber,
                               Long departmentId,
                               String departmentName,
                               String position,
                               LocalDate hireDate,
                               String statusDescription,
                               Long profileImageId) {


}
