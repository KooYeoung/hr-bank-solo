package com.kooyeoung.hrbank.dto.request.department;

import com.kooyeoung.hrbank.dto.command.department.DepartmentCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DepartmentCreateRequest(
        @NotBlank
        @Size(max = 100)
        String name,
        @Size(max = 500)
        String description,
        @NotNull
        LocalDate establishedDate
) {

    public DepartmentCreateCommand toCommand() {
        return new DepartmentCreateCommand(
                name,
                description,
                establishedDate
        );
    }
}
