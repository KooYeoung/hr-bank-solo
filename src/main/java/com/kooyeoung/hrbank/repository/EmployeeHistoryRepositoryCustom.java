package com.kooyeoung.hrbank.repository;

import com.kooyeoung.hrbank.dto.repository.employeeHistory.EmployeeHistorySearchCondition;
import com.kooyeoung.hrbank.dto.repository.employeeHistory.EmployeeHistorySummary;

import java.util.List;

public interface EmployeeHistoryRepositoryCustom {
    List<EmployeeHistorySummary> searchEmployeeHistory(EmployeeHistorySearchCondition condition);
    long countEmployeeHistory(EmployeeHistorySearchCondition condition);
}
