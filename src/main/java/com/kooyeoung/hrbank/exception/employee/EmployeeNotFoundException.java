package com.kooyeoung.hrbank.exception.employee;

import com.kooyeoung.hrbank.exception.CustomNotFoundException;

public class EmployeeNotFoundException extends CustomNotFoundException {
    public EmployeeNotFoundException(Long employeeId) {
        super("존재하지 않는 사원입니다. employeeId=" + employeeId);
    }
}
