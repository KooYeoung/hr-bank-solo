package com.kooyeoung.hrbank.repository;

import com.kooyeoung.hrbank.dto.repository.DepartmentSearchCondition;
import com.kooyeoung.hrbank.dto.repository.DepartmentSummary;
import com.kooyeoung.hrbank.entity.Department;

import java.util.List;

public interface DepartmentRepositoryCustom {
    List<DepartmentSummary> searchDepartment(DepartmentSearchCondition condition);

}
