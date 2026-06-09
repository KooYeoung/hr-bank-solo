package com.kooyeoung.hrbank.dto.command.historyDetail;

import com.kooyeoung.hrbank.entity.EmployeeHistory;
import com.kooyeoung.hrbank.entity.snapshot.EmployeeSnapshot;

public record EmployeeHistoryDetailCommand(
        EmployeeSnapshot beforeSnapshot
        , EmployeeSnapshot afterSnapshot
        , EmployeeHistory history
        ) {
}
