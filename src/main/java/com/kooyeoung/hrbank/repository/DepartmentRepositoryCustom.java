package com.kooyeoung.hrbank.repository;

import com.kooyeoung.hrbank.dto.repository.DepartmentSearchCondition;
import com.kooyeoung.hrbank.dto.repository.DepartmentSummary;
import com.kooyeoung.hrbank.entity.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepositoryCustom {
    List<DepartmentSummary> searchDepartment(DepartmentSearchCondition condition);
    Optional<DepartmentSummary> findSummaryById(Long id);
    long countDepartment(DepartmentSearchCondition condition);
}
