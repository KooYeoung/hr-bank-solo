package com.kooyeoung.hrbank.repository;

import com.kooyeoung.hrbank.dto.repository.employee.EmployeeSearchCondition;
import com.kooyeoung.hrbank.dto.repository.employee.EmployeeSummary;
import com.kooyeoung.hrbank.dto.repository.employee.EmployeesCountCondition;
import com.kooyeoung.hrbank.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepositoryCustom  {
    List<EmployeeSummary> searchEmployee(EmployeeSearchCondition condition);
    long countEmployee(EmployeeSearchCondition condition);
    long countEmployee(EmployeesCountCondition condition);
}
