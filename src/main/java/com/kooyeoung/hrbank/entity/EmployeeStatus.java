package com.kooyeoung.hrbank.entity;

import com.kooyeoung.hrbank.exception.employee.InvalidEmployeeStatusException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum EmployeeStatus {
    ACTIVE("재직중"),
    ON_LEAVE("휴직중"),
    RESIGNED("퇴사");

    private final String description;

    EmployeeStatus(String description) {
        this.description = description;
    }

    private final static Set<String> AVAILABLE_STATUS = Arrays.stream(EmployeeStatus.values())
            .map(Enum::name)
            .collect(Collectors.toUnmodifiableSet());

    public static EmployeeStatus from(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidEmployeeStatusException("직원 상태는 필수값입니다.");
        }
        String normalizedStatus = value.trim().toUpperCase();

        if (!AVAILABLE_STATUS.contains(normalizedStatus)) {
            throw new InvalidEmployeeStatusException("유효하지 않은 직원 상태입니다. status=" + value);
        }

        return EmployeeStatus.valueOf(normalizedStatus);
    }
}
