package com.kooyeoung.hrbank.entity;

import com.kooyeoung.hrbank.exception.backupInfo.InvalidBackupStatusException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum BackupStatus {
    IN_PROGRESS("진행중"), COMPLETED("완료"), FAILED("실패"), SKIPPED("건너뜀");
    private final String label;

    BackupStatus(String label) {
        this.label = label;
    }

    private static final Set<String> AVAILABLE_STATUS = Arrays.stream(BackupStatus.values())
            .map(Enum::name)
            .collect(Collectors.toUnmodifiableSet());

    public static BackupStatus getBackupStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new InvalidBackupStatusException("백업 상태 값이 존재하지 않습니다.");
        }

        String normalizedStatus = status.trim().toUpperCase();
        if (!AVAILABLE_STATUS.contains(normalizedStatus)) {
            throw new InvalidBackupStatusException("지원하지 않는 백업 상태 값입니다. status=" + status);
        }

        return BackupStatus.valueOf(normalizedStatus);

    }
}
