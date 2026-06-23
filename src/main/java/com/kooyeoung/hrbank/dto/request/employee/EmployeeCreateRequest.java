package com.kooyeoung.hrbank.dto.request.employee;

import com.kooyeoung.hrbank.dto.command.employee.EmployeeCreateCommand;
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
    public EmployeeCreateCommand toCommand() {
        return new EmployeeCreateCommand(
                name,
                email,
                position,
                hireDate
        );
    }


}
