package com.kooyeoung.hrbank.exception.fileInfo;

import com.kooyeoung.hrbank.exception.CustomBadRequestException;

public class InvalidFileNameException extends CustomBadRequestException {
    public InvalidFileNameException() {
        super("파일명이 존재하지 않습니다.");
    }
}
