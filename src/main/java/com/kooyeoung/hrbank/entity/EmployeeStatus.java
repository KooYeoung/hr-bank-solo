package com.kooyeoung.hrbank.entity;

import lombok.Getter;

@Getter
public enum EmployeeStatus {
    EMPLOYED("재직중"),
    ON_LEAVE("휴직중"),
    RESIGNED("퇴사");

    private final String description;

    EmployeeStatus(String description){
        this.description = description;
    }

    public static EmployeeStatus from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("직원 상태는 필수값입니다.");
        }

        try {
            return EmployeeStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 직원 상태입니다.");
        }
    }
}
