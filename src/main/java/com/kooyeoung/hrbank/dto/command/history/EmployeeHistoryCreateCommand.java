package com.kooyeoung.hrbank.dto.command.history;

import com.kooyeoung.hrbank.entity.HistoryType;
import com.kooyeoung.hrbank.entity.snapshot.EmployeeSnapshot;

public record EmployeeHistoryCreateCommand(
        HistoryType type,
        EmployeeSnapshot beforeSnapshot,
        EmployeeSnapshot afterSnapshot,
        String memo
) {

    public static EmployeeHistoryCreateCommand create(EmployeeSnapshot beforeSnapshot, EmployeeSnapshot afterSnapshot, String memo) {
        if (beforeSnapshot == null && afterSnapshot == null)
            throw new IllegalArgumentException("변경사항에 대하 둘다 null 일수 없습니다.");

        if (beforeSnapshot == null) {
            return new EmployeeHistoryCreateCommand(HistoryType.CREATED, null, afterSnapshot, memo);
        }

        if (afterSnapshot == null) {
            return new EmployeeHistoryCreateCommand(HistoryType.DELETED, beforeSnapshot, null, memo);
        }

        return new EmployeeHistoryCreateCommand(HistoryType.UPDATED, beforeSnapshot, afterSnapshot, memo);

    }

    public String getEmployeeNumber() {
        if (existEmployeeNumber(beforeSnapshot)) {
            return beforeSnapshot.employeeNumber();
        }
        if (existEmployeeNumber(afterSnapshot)) {
            return afterSnapshot.employeeNumber();
        }

        throw new IllegalArgumentException("사원번호가 존재하지 않습니다.");
    }

    private boolean existEmployeeNumber(EmployeeSnapshot snapshot) {
        return snapshot != null && snapshot.employeeNumber() != null && !snapshot.employeeNumber().isBlank();
    }
}
