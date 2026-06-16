package com.kooyeoung.hrbank.service;

import com.kooyeoung.hrbank.entity.BackupInfo;
import com.kooyeoung.hrbank.entity.FileInfo;
import com.kooyeoung.hrbank.repository.BackupInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 *  백업 상태 변경은 실패 상황에서도 독립적으로 커밋되어야 하므로
 *  모든 public 메서드를 REQUIRES_NEW 트랜잭션으로 실행한다.
 */
@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class BackupInfoCommandService {
    private final BackupInfoRepository repository;

    public BackupInfo skip(String worker){
        BackupInfo currentSkippedBackup = BackupInfo.skippedBackupInfo(worker, LocalDateTime.now());

       return repository.save(currentSkippedBackup);

    }

    public BackupInfo createInProgress(String worker){
        BackupInfo inProgressBackup = BackupInfo.inProgressBackupInfo(worker, LocalDateTime.now());
        return repository.save(inProgressBackup);

    }

    public BackupInfo complete(Long backupId, FileInfo backupFile){
        BackupInfo backupInfo = getBackupInfoById(backupId);

        backupInfo.complete(backupFile);
        return backupInfo;
    }

    public BackupInfo fail(Long backupId, FileInfo logFile){
        BackupInfo backupInfo = getBackupInfoById(backupId);

        backupInfo.fail(logFile);
        return backupInfo;
    }

    @NonNull
    private BackupInfo getBackupInfoById(Long backupId) {
        return repository.findById(backupId)
                .orElseThrow(() -> new IllegalArgumentException("백업 이력을 찾을수 없습니다."));
    }
}
