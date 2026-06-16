package com.kooyeoung.hrbank.dto.repository.backupInfo;

import com.kooyeoung.hrbank.entity.BackupStatus;

import java.time.LocalDateTime;

public record BackupInfoSummery(
        Long id,
        String worker,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        BackupStatus status,
        Long fileId
) {
}
