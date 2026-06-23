package com.kooyeoung.hrbank.service;

import com.kooyeoung.hrbank.dto.repository.backupInfo.BackupInfoSearchCondition;
import com.kooyeoung.hrbank.dto.repository.backupInfo.BackupInfoSummery;
import com.kooyeoung.hrbank.dto.response.BackupInfoDto;
import com.kooyeoung.hrbank.dto.response.PageResponse;
import com.kooyeoung.hrbank.entity.*;
import com.kooyeoung.hrbank.entity.snapshot.EmployeeSnapshot;
import com.kooyeoung.hrbank.exception.backupInfo.BackupInfoNotFoundException;
import com.kooyeoung.hrbank.repository.BackupInfoRepository;
import com.kooyeoung.hrbank.repository.EmployeeHistoryRepository;
import com.kooyeoung.hrbank.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackupService {

    private final EmployeeHistoryRepository historyRepository;
    private final BackupInfoRepository backupInfoRepository;
    private final EmployeeRepository employeeRepository;
    private final IpAddressService ipAddressService;
    private final FileInfoService fileInfoService;
    private final BackupInfoCommandService backupInfoCommandService;

    public BackupInfoDto save() {

        return save(null);
    }

    public BackupInfoDto save(String batchWorker) {

        String worker = resolveWorker(batchWorker);

        LocalDateTime latestCreatedAt =
                historyRepository.findTopByOrderByCreatedAtDesc()
                        .map(EmployeeHistory::getCreatedAt).orElse(null);

        LocalDateTime lastBackupStartTime =
                backupInfoRepository.findTopByStatusOrderByStartedAtDesc(BackupStatus.COMPLETED)
                        .map(BackupInfo::getStartedAt).orElse(null);

        if (!isBackupRequired(latestCreatedAt, lastBackupStartTime)) {
            BackupInfo skip = backupInfoCommandService.skip(worker);
            return BackupInfoDto.from(skip);
        }

        BackupInfo backupInfo = backupInfoCommandService.createInProgress(worker);

        try {
            FileInfo backupFile = createEmployeeBackupCsv();

            BackupInfo complete = backupInfoCommandService.complete(backupInfo.getId(), backupFile);
            return BackupInfoDto.from(complete);
        } catch (Exception e) {
            log.error("직원 데이터 백업 실패", e);

            FileInfo logFile = null;

            try {
                logFile = createBackupErrorLog(e);
            } catch (Exception logException) {
                log.error("백업 실패 로그 파일 생성 실패", logException);
            }

            BackupInfo fail = backupInfoCommandService.fail(backupInfo.getId(), logFile);
            return BackupInfoDto.from(fail);
        }

    }

    @Transactional(readOnly = true)
    public PageResponse<BackupInfoDto> list(BackupInfoSearchCondition condition) {

        int size = condition.size();
        List<BackupInfoSummery> backupInfoSummaries = backupInfoRepository.searchBackupInfo(condition);
        boolean hasNext = backupInfoSummaries.size() > size;

        List<BackupInfoSummery> pageContent = hasNext ? backupInfoSummaries.subList(0, size) : backupInfoSummaries;

        List<BackupInfoDto> content = pageContent.stream()
                .map(BackupInfoDto::from)
                .toList();

        String nextCursor = null;
        Long nextIdAfter = null;

        if (hasNext && !pageContent.isEmpty()) {
            BackupInfoSummery last = pageContent.get(pageContent.size() - 1);
            nextCursor = getNextCursor(condition.sortField(), last);
            nextIdAfter = last.id();
        }
        Long totalBackupCount = backupInfoRepository.countBackupInfo(condition);

        return new PageResponse<>(
                content,
                nextCursor,
                nextIdAfter,
                size,
                totalBackupCount,
                hasNext
        );

    }

    @Transactional(readOnly = true)
    public BackupInfoDto getLatestFrom(BackupStatus status) {

        BackupInfo backupInfo = backupInfoRepository
                .findTopByStatusOrderByStartedAtDesc(status)
                .orElseThrow(() -> new BackupInfoNotFoundException(status));

        return BackupInfoDto.from(backupInfo);
    }

    private String getNextCursor(String sortField, BackupInfoSummery last) {

        if ("endedAt".equals(sortField)) {
            return last.endedAt() == null ? null : last.endedAt().toString();
        }

        return last.startedAt().toString();
    }

    private String resolveWorker(String batchWorker) {
        if (batchWorker == null || batchWorker.isBlank()) {
            return ipAddressService.getClientIp();
        }

        return batchWorker;
    }

    private boolean isBackupRequired(
            LocalDateTime lastHistoryCreatedAt,
            LocalDateTime lastBackupStartedAt
    ) {
        if (lastHistoryCreatedAt == null) {
            return false;
        }

        if (lastBackupStartedAt == null) {
            return true;
        }

        return lastHistoryCreatedAt.isAfter(lastBackupStartedAt);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

    private FileInfo createEmployeeBackupCsv() {
        String fileName = "employee-backup-" + formatDateTime(LocalDateTime.now()) + ".csv";

        return fileInfoService.saveGeneratedFile(
                fileName,
                "text/csv",
                FileType.BACKUP_EMPLOYEE_CSV,
                path -> {
                    try (
                            BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
                            CSVPrinter csvPrinter = new CSVPrinter(
                                    writer,
                                    CSVFormat.DEFAULT.builder()
                                            .setHeader(
                                                    "id",
                                                    "employeeNumber",
                                                    "name",
                                                    "email",
                                                    "department",
                                                    "position",
                                                    "hireDate",
                                                    "status"
                                            )
                                            .build()
                            )
                    ) {
                        int page = 0;
                        int size = 500;

                        Page<Employee> employees;

                        do {
                            Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
                            employees = employeeRepository.findAllBy(pageable);

                            for (Employee employee : employees.getContent()) {
                                EmployeeSnapshot snapshot = employee.snapshot();

                                csvPrinter.printRecord(
                                        snapshot.id(),
                                        snapshot.employeeNumber(),
                                        snapshot.name(),
                                        snapshot.email(),
                                        snapshot.departmentName(),
                                        snapshot.position(),
                                        snapshot.hireDate(),
                                        snapshot.statusDescription()
                                );
                            }

                            page++;
                        } while (employees.hasNext());

                        csvPrinter.flush();
                    }
                }
        );
    }

    private FileInfo createBackupErrorLog(Exception e) {
        String fileName = "backup-error-" + formatDateTime(LocalDateTime.now()) + ".log";

        return fileInfoService.saveGeneratedFile(
                fileName,
                "text/plain",
                FileType.BACKUP_ERROR_LOG,
                path -> {
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter);

                    printWriter.println("직원 데이터 백업 실패");
                    printWriter.println("failedAt=" + LocalDateTime.now());
                    printWriter.println("message=" + e.getMessage());
                    printWriter.println();
                    e.printStackTrace(printWriter);

                    Files.writeString(
                            path,
                            stringWriter.toString(),
                            StandardCharsets.UTF_8
                    );
                }
        );

    }


}
