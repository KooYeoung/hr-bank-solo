package com.kooyeoung.hrbank.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DepartmentUpdateRequest(
        @NotNull
        @NotEmpty
        String name,
        @NotNull
        String description,
        @NotNull
        @NotEmpty
        LocalDate establishedDate
) {
}
