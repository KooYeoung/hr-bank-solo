package com.kooyeoung.hrbank.repository;

import com.kooyeoung.hrbank.dto.repository.backupInfo.BackupInfoSearchCondition;
import com.kooyeoung.hrbank.dto.repository.backupInfo.BackupInfoSummery;

import java.util.List;

public interface BackupInfoRepositoryCustom {
    List<BackupInfoSummery> searchBackupInfo(BackupInfoSearchCondition condition);

    Long countBackupInfo(BackupInfoSearchCondition condition);
}
