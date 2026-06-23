package com.kooyeoung.hrbank.exception.fileInfo;

import com.kooyeoung.hrbank.exception.CustomBadRequestException;

public class UnsupportedFileTypeException extends CustomBadRequestException {
    public UnsupportedFileTypeException(String message) {
        super(message);
    }
}
