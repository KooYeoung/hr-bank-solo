package com.kooyeoung.hrbank.exception.employeeHistory;

import com.kooyeoung.hrbank.exception.CustomNotFoundException;

public class EmployeeHistoryNotFoundException extends CustomNotFoundException {
    public EmployeeHistoryNotFoundException(Long employeeHistoryId) {
        super("수정이력이 존재하지 않습니다. employeeHistoryId = " + employeeHistoryId);
    }
}
