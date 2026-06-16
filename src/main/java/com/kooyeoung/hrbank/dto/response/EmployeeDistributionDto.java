package com.kooyeoung.hrbank.dto.response;

public record EmployeeDistributionDto(
        String groupKey,
        long count,
        double percentage
) {

}
