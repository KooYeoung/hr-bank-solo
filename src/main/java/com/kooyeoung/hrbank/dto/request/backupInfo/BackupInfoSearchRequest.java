package com.kooyeoung.hrbank.dto.request.backupInfo;

import com.kooyeoung.hrbank.dto.repository.backupInfo.BackupInfoSearchCondition;
import com.kooyeoung.hrbank.entity.BackupStatus;

import java.time.LocalDateTime;
import java.util.Set;

public record BackupInfoSearchRequest(
        String worker,
        String status,
        LocalDateTime startAtFrom,
        LocalDateTime startAtTo,
        Long idAfter,
        String cursor,
        Integer size,
        String sortField,
        String sortDirection
) {

    private static final String DEFAULT_DIRECTION = "desc";
    private static final String DEFAULT_SORT_FIELD = "startedAt";
    private static final Set<String> SORT_FIELDS = Set.of(DEFAULT_SORT_FIELD, "endedAt");
    private static final Set<String> SORT_DIRECTIONS = Set.of("asc", DEFAULT_DIRECTION);

    public int getSizeOrDefault() {
        return size == null || size <= 0 ? 10 : size;
    }

    public String getSortFieldOrDefault() {
        if (sortField == null || sortField.isBlank()) {
            return DEFAULT_SORT_FIELD;
        }

        String normalizedSortField = sortField.trim();

        return !SORT_FIELDS.contains(normalizedSortField) ? DEFAULT_SORT_FIELD : normalizedSortField;
    }

    public String getSortDirectionOrDefault() {
        if (sortDirection == null || sortDirection.isBlank()) return DEFAULT_DIRECTION;

        String normalizedSortDirection = sortDirection.trim().toLowerCase();

        return !SORT_DIRECTIONS.contains(normalizedSortDirection) ? DEFAULT_DIRECTION : normalizedSortDirection;
    }

    public boolean isDesc() {
        return "desc".equalsIgnoreCase(getSortDirectionOrDefault());
    }

    public boolean hasCursor() {
        return idAfter != null && cursor != null && !cursor.isBlank();
    }

    public BackupStatus getStatusOrDefault() {
        if (status == null || status.isBlank()) return null;

        return BackupStatus.getBackupStatus(status);
    }

    public BackupInfoSearchCondition toCondition() {
        return new BackupInfoSearchCondition(
                worker,
                getStatusOrDefault(),
                startAtFrom,
                startAtTo,
                getSortFieldOrDefault(),
                cursor,
                idAfter,
                hasCursor(),
                isDesc(),
                getSizeOrDefault()
        );
    }
}
