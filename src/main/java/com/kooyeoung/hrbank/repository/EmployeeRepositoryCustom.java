package com.kooyeoung.hrbank.repository;

import com.kooyeoung.hrbank.dto.repository.employee.EmployeeSearchCondition;
import com.kooyeoung.hrbank.dto.repository.employee.EmployeeSummary;
import com.kooyeoung.hrbank.dto.repository.employee.EmployeesCountCondition;

import java.util.List;

public interface EmployeeRepositoryCustom {
    List<EmployeeSummary> searchEmployee(EmployeeSearchCondition condition);

    long countEmployee(EmployeeSearchCondition condition);

    long countEmployee(EmployeesCountCondition condition);
}
