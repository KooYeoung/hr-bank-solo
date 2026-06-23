package com.kooyeoung.hrbank.dto.repository.backupInfo;

import com.kooyeoung.hrbank.entity.BackupStatus;

import java.time.LocalDateTime;

public record BackupInfoSearchCondition(
        String worker
        , BackupStatus status
        , LocalDateTime startAtFrom
        , LocalDateTime startAtTo
        , String sortField
        , String cursor
        , Long idAfter
        , boolean hasCursor
        , boolean isDesc
        , int size
) {

}
