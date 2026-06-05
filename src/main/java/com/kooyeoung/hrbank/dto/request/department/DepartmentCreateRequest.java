package com.kooyeoung.hrbank.dto.request.department;

import com.kooyeoung.hrbank.entity.Department;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DepartmentCreateRequest(
        @NotNull
        @NotEmpty
        String name,
        @NotNull
        String description,
        @NotNull
        @NotEmpty
        LocalDate establishedDate
) {

    public Department toDepartment(){
        return new Department(name, description, establishedDate);
    }

}
