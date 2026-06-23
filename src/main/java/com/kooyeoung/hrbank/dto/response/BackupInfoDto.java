package com.kooyeoung.hrbank.dto.response;

import com.kooyeoung.hrbank.dto.repository.backupInfo.BackupInfoSummery;
import com.kooyeoung.hrbank.entity.BackupInfo;

import java.time.LocalDateTime;

public record BackupInfoDto(
        Long id,
        String worker,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        String status,
        Long fileId
) {

    public static BackupInfoDto from(BackupInfoSummery summery){
        return new BackupInfoDto(
                summery.id(),
                summery.worker(),
                summery.startedAt(),
                summery.endedAt(),
                summery.status().getLabel(),
                summery.fileId()
        );
    }

    public static BackupInfoDto from(BackupInfo backupInfo){
        return new BackupInfoDto(
                backupInfo.getId(),
                backupInfo.getWorker(),
                backupInfo.getStartedAt(),
                backupInfo.getEndedAt(),
                backupInfo.getStatus().getLabel(),
                backupInfo.getBackupFileId()
        );
    }
}
