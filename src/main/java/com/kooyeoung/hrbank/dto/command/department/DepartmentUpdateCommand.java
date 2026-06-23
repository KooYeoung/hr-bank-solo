package com.kooyeoung.hrbank.dto.command.department;

import java.time.LocalDate;

public record DepartmentUpdateCommand(
        String name,
        String description,
        LocalDate establishedDate
) {

}
