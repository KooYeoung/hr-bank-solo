package com.kooyeoung.hrbank.dto.response;

import java.time.LocalDate;

public record EmployeeTrendDto(
        LocalDate date,
        long count,
        long change,
        double changeRate
) {
}
