package com.kooyeoung.hrbank.exception.employee;

import com.kooyeoung.hrbank.exception.CustomBadRequestException;

public class InvalidEmployeeStatusException extends CustomBadRequestException {
    public InvalidEmployeeStatusException(String message) {
        super(message);
    }
}
