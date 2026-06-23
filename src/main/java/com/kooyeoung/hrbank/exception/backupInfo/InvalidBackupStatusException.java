package com.kooyeoung.hrbank.exception.backupInfo;

import com.kooyeoung.hrbank.exception.CustomBadRequestException;

public class InvalidBackupStatusException extends CustomBadRequestException {
    public InvalidBackupStatusException(String message) {
        super(message);
    }
}
