package com.kooyeoung.hrbank.exception.employeeHistory;

import com.kooyeoung.hrbank.exception.CustomBadRequestException;

public class InvalidHistoryTypeException extends CustomBadRequestException {
    public InvalidHistoryTypeException(String message) {
        super(message);
    }
}
