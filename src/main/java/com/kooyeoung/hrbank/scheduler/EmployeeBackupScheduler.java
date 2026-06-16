package com.kooyeoung.hrbank.scheduler;

import com.kooyeoung.hrbank.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeBackupScheduler {
    private final BackupService backupService;

    @Value("${hrbank.backup.schedule.enabled:true}")
    private boolean enabled;

    @Scheduled(fixedDelayString = "${hrbank.backup.schedule.fixed-delay-ms:3600000}")
    public void runEmployeeBackup() {
        if (!enabled) {
            log.debug("직원 데이터 자동 백업 스케줄러 비활성화 상태");
            return;
        }

        log.info("직원 데이터 자동 백업 스케줄러 실행");

        backupService.save("system");
    }

}
