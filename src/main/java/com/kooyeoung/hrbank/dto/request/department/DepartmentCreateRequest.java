package com.kooyeoung.hrbank.dto.request.department;

import com.kooyeoung.hrbank.entity.Department;
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

    public Department toDepartment(){
        return new Department(name, description, establishedDate);
    }

}
