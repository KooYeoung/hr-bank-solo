package com.kooyeoung.hrbank.service;

import com.kooyeoung.hrbank.dto.command.history.EmployeeHistoryCreateCommand;
import com.kooyeoung.hrbank.dto.command.historyDetail.EmployeeHistoryDetailCommand;
import com.kooyeoung.hrbank.dto.repository.employeeHistory.EmployeeHistoryEditCountCondition;
import com.kooyeoung.hrbank.dto.repository.employeeHistory.EmployeeHistorySearchCondition;
import com.kooyeoung.hrbank.dto.repository.employeeHistory.EmployeeHistorySummary;
import com.kooyeoung.hrbank.dto.response.ChangeLogDetailDto;
import com.kooyeoung.hrbank.dto.response.ChangeLogDetailRowDto;
import com.kooyeoung.hrbank.dto.response.ChangeLogDto;
import com.kooyeoung.hrbank.dto.response.PageResponse;
import com.kooyeoung.hrbank.entity.EmployeeHistory;
import com.kooyeoung.hrbank.exception.employeeHistory.EmployeeHistoryNotFoundException;
import com.kooyeoung.hrbank.repository.EmployeeHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmployeeHistoryService {

    private final IpAddressService ipAddressService;
    private final EmployeeHistoryRepository repository;
    private final EmployeeHistoryDetailService historyDetailService;

    @Transactional
    public void save(EmployeeHistoryCreateCommand command) {
        String clientIp = ipAddressService.getClientIp();

        EmployeeHistory history = new EmployeeHistory(
                command.type(),
                command.getEmployeeNumber(),
                command.memo(),
                clientIp
        );

        repository.save(history);

        historyDetailService.save(
                new EmployeeHistoryDetailCommand(
                        command.beforeSnapshot(),
                        command.afterSnapshot(),
                        history
                )
        );

    }

    public PageResponse<ChangeLogDto> list(EmployeeHistorySearchCondition condition) {

        int size = condition.size();
        List<EmployeeHistorySummary> employeeHistorySummaries = repository.searchEmployeeHistory(condition);

        boolean hasNext = employeeHistorySummaries.size() > size;

        List<EmployeeHistorySummary> pageContent = hasNext ? employeeHistorySummaries.subList(0, size) : employeeHistorySummaries;

        List<ChangeLogDto> content = pageContent.stream()
                .map(ChangeLogDto::from)
                .toList();

        String nextCursor = null;
        Long nextIdAfter = null;
        if (hasNext && !pageContent.isEmpty()) {
            EmployeeHistorySummary last = pageContent.get(pageContent.size() - 1);

            nextCursor = getNextCursor(condition.sortField(), last);
            nextIdAfter = last.id();
        }

        long totalCounts = repository.countEmployeeHistory(condition);

        return new PageResponse<>(
                content,
                nextCursor,
                nextIdAfter,
                size,
                totalCounts,
                hasNext
        );
    }

    private String getNextCursor(String sortFiled, EmployeeHistorySummary last) {
        if ("at".equals(sortFiled)) {
            return last.at().toString();
        }

        return last.ipAddress();
    }

    public ChangeLogDetailDto detail(Long id) {

        repository.findById(id)
                .orElseThrow(()-> new EmployeeHistoryNotFoundException(id));

        List<ChangeLogDetailRowDto> rows = repository.findDetailRowsById(id);

        return ChangeLogDetailDto.fromRows(rows);
    }

    public Long editCount(EmployeeHistoryEditCountCondition condition) {
        Optional<Long> editCount = repository.countByCreatedAtBetween(condition.fromDate(), condition.toDate());

        return editCount.isPresent() ? editCount.get() : 0;
    }


}
