package com.kooyeoung.hrbank.repository;

import com.kooyeoung.hrbank.entity.BackupInfo;
import com.kooyeoung.hrbank.entity.BackupStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BackupInfoRepository extends JpaRepository<BackupInfo, Long> ,BackupInfoRepositoryCustom{

    Optional<BackupInfo> findTopByStatusOrderByStartedAtDesc(BackupStatus status);
}
