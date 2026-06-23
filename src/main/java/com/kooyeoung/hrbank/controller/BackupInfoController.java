package com.kooyeoung.hrbank.controller;

import com.kooyeoung.hrbank.dto.request.backupInfo.BackupInfoSearchRequest;
import com.kooyeoung.hrbank.dto.response.BackupInfoDto;
import com.kooyeoung.hrbank.dto.response.PageResponse;
import com.kooyeoung.hrbank.entity.BackupStatus;
import com.kooyeoung.hrbank.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/backups")
@RestController
@Slf4j
@RequiredArgsConstructor
public class BackupInfoController {
    private final BackupService backupService;

    @GetMapping
    public ResponseEntity<PageResponse<BackupInfoDto>> list(BackupInfoSearchRequest request){
        PageResponse<BackupInfoDto> backupInfoPage = backupService.list(request.toCondition());

        return ResponseEntity.ok().body(backupInfoPage);
    }

    @PostMapping
    public ResponseEntity<BackupInfoDto> save(){
        BackupInfoDto save = backupService.save();

        return ResponseEntity.status(HttpStatus.CREATED).body(save);
    }

    @GetMapping("/latest")
    public ResponseEntity<BackupInfoDto> lastBackup(@RequestParam(defaultValue = "COMPLETED") String status){
        BackupInfoDto latestFrom = backupService.getLatestFrom(BackupStatus.getBackupStatus(status));

        return ResponseEntity.ok().body(latestFrom);
    }
}
