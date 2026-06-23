package com.kooyeoung.hrbank.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BackupInfo {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String worker;

    @Getter
    private LocalDateTime startedAt;

    @Getter
    private LocalDateTime endedAt;

    @Getter
    @Enumerated(EnumType.STRING)
    private BackupStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private FileInfo backupFile;

    public static BackupInfo skippedBackupInfo(String worker, LocalDateTime now) {
        return new BackupInfo(worker, now, now, BackupStatus.SKIPPED);
    }

    public static BackupInfo inProgressBackupInfo(String worker, LocalDateTime now) {
        return new BackupInfo(worker, now, null, BackupStatus.IN_PROGRESS);
    }

    private BackupInfo(String worker, LocalDateTime startedAt, LocalDateTime endedAt, BackupStatus status) {
        this.worker = worker;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.status = status;
    }

    public void complete(FileInfo backupFile) {
        this.backupFile = backupFile;
        this.status = BackupStatus.COMPLETED;
        this.endedAt = LocalDateTime.now();
    }

    public void fail(FileInfo logFile) {
        this.backupFile = logFile;
        this.status = BackupStatus.FAILED;
        this.endedAt = LocalDateTime.now();
    }

    public Long getBackupFileId() {
        if (backupFile == null) return null;
        return backupFile.getId();
    }
}
