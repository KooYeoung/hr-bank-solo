package com.kooyeoung.hrbank.exception.employee;

import com.kooyeoung.hrbank.exception.CustomBadRequestException;

public class EmployeeEmailAlreadyExistsException extends CustomBadRequestException {
    public EmployeeEmailAlreadyExistsException(String email) {
        super("이미 존재하는 이메일입니다. email=" + email);
    }
}
