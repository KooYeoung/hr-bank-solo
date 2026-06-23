package com.kooyeoung.hrbank.dto.request.employee;

import com.kooyeoung.hrbank.dto.command.employee.EmployeeUpdateCommand;
import com.kooyeoung.hrbank.entity.EmployeeStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record EmployeeUpdateRequest(
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
        @NotBlank
        String status,
        @Size(max = 500)
        String memo
) {
    public EmployeeUpdateCommand toCommand() {
        return new EmployeeUpdateCommand(
                name,
                email,
                position,
                hireDate,
                EmployeeStatus.from(status)
        );
    }

}
