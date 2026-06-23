package com.kooyeoung.hrbank.dto.repository.department;

public record DepartmentSearchCondition(
        String keyword,
        String sortField,
        String cursor,
        Long idAfter,
        Boolean hasCursor,
        Boolean isDesc,
        Integer size
) {

}
