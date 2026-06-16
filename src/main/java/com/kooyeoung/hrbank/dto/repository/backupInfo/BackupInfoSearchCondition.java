package com.kooyeoung.hrbank.dto.repository.backupInfo;

import java.time.LocalDateTime;

public record BackupInfoSearchCondition(
        String worker
        , String status
        , LocalDateTime startAtFrom
        , LocalDateTime startAtTo
        , String sortField
        , String cursor
        , long idAfter
        , boolean hasCursor
        , boolean isDesc
        , int size
) {

}
