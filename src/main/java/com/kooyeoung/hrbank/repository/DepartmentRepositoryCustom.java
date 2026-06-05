package com.kooyeoung.hrbank.repository;

import com.kooyeoung.hrbank.dto.repository.department.DepartmentSearchCondition;
import com.kooyeoung.hrbank.dto.repository.department.DepartmentSummary;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepositoryCustom {
    List<DepartmentSummary> searchDepartment(DepartmentSearchCondition condition);
    Optional<DepartmentSummary> findSummaryById(Long id);
    long countDepartment(DepartmentSearchCondition condition);
}
