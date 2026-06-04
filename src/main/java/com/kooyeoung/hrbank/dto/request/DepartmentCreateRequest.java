package com.kooyeoung.hrbank.dto.request;

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
        String establishedDate
) {

    public Department toDepartment(){
        return new Department(name, description, LocalDate.parse(establishedDate));
    }

}
