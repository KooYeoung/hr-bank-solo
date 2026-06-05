package com.kooyeoung.hrbank.dto.request.employee;

import com.kooyeoung.hrbank.dto.command.employee.EmployeeCreateCommand;
import com.kooyeoung.hrbank.entity.Department;
import com.kooyeoung.hrbank.entity.Employee;
import com.kooyeoung.hrbank.entity.FileInfo;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record EmployeeCreateRequest(
        @NotBlank
        String name,
        @NotBlank
        @Email
        String email,
        @NotNull
        @Positive
        Long departmentId,
        @NotBlank
        String position,
        @NotNull
        @PastOrPresent // 미래 입사자 제외
        LocalDate hireDate,
        @Size(max = 500)
        String memo
) {
    public EmployeeCreateCommand toCommand(Department department, FileInfo profileImage){
        return new EmployeeCreateCommand(department
        ,name
        ,email
        ,position
        ,hireDate
        ,profileImage);
    }

    public EmployeeCreateCommand toCommand(Department department){
        return this.toCommand(department, null);
    }


}
