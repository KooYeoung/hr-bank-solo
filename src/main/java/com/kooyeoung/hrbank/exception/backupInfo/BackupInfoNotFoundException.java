package com.kooyeoung.hrbank.exception.backupInfo;

import com.kooyeoung.hrbank.entity.BackupStatus;
import com.kooyeoung.hrbank.exception.CustomNotFoundException;

public class BackupInfoNotFoundException extends CustomNotFoundException {
    public BackupInfoNotFoundException(Long backupInfoId) {
        super("백업 이력을 찾을수 없습니다. backupInfoId = " + backupInfoId);
    }

    public BackupInfoNotFoundException(BackupStatus status) {
        super(status.getLabel() + "의 백업정보가 존재하지 않습니다.");
    }
}
