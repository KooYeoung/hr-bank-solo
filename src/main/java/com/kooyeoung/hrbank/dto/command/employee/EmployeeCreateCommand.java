package com.kooyeoung.hrbank.dto.command.employee;

import com.kooyeoung.hrbank.entity.Department;
import com.kooyeoung.hrbank.entity.FileInfo;

import java.time.LocalDate;

public record EmployeeCreateCommand (
        Department department
        , String name
        , String email
        , String position
        , LocalDate hireDate
        , FileInfo profileImage
){
}
