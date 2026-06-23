package com.kooyeoung.hrbank.dto.repository.department;

public record DepartmentSearchCondition(
        String keyword,
        String sortField,
        String cursor,
        long idAfter,
        boolean hasCursor,
        boolean isDesc,
        int size
) {

}
