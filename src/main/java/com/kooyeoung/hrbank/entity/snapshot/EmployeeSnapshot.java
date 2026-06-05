package com.kooyeoung.hrbank.entity.snapshot;

import com.kooyeoung.hrbank.dto.response.EmployeeDto;

import java.time.LocalDate;

public record EmployeeSnapshot(Long id,
                               String name,
                               String email,
                               String employeeNumber,
                               Long departmentId,
                               String departmentName,
                               String position,
                               LocalDate hireDate,
                               String status,
                               Long profileImageId) {


}
