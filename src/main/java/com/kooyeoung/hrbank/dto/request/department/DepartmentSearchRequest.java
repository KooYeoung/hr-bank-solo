package com.kooyeoung.hrbank.dto.request.department;

import com.kooyeoung.hrbank.dto.repository.department.DepartmentSearchCondition;

import java.util.Set;

public record DepartmentSearchRequest(
        String nameOrDescription,
        Long idAfter,
        String cursor,
        Integer size,
        String sortField,
        String sortDirection
) {

    private final static String DEFAULT_SORT_FIELD = "name";
    private final static String DEFAULT_SORT_DIRECTION = "asc";
    private final static Set<String> SORT_FIELDS = Set.of(DEFAULT_SORT_FIELD, "establishedDate");
    private final static Set<String> SORT_DIRECTIONS = Set.of(DEFAULT_SORT_DIRECTION, "desc");

    public int getSizeOrDefault() {
        return size == null || size <= 0 ? 10 : size;
    }

    public String getSortFieldOrDefault() {
        if (sortField == null || sortField.isBlank()) return DEFAULT_SORT_FIELD;

        String normalizedSortField = sortField.trim();

        return !SORT_FIELDS.contains(normalizedSortField) ? DEFAULT_SORT_FIELD : normalizedSortField;
    }

    public String getSortDirectionOrDefault() {
        if (sortDirection == null || sortDirection.isBlank()) return DEFAULT_SORT_DIRECTION;

        String normalizedSortDirection = sortDirection.trim().toLowerCase();

        return !SORT_DIRECTIONS.contains(normalizedSortDirection) ? DEFAULT_SORT_DIRECTION : normalizedSortDirection;
    }

    public boolean isDesc() {
        return "desc".equalsIgnoreCase(getSortDirectionOrDefault());
    }

    public boolean hasCursor() {
        return idAfter != null && cursor != null && !cursor.isBlank();
    }

    public DepartmentSearchCondition toCondition() {
        return new DepartmentSearchCondition(
                nameOrDescription,
                getSortFieldOrDefault(),
                cursor,
                idAfter,
                hasCursor(),
                isDesc(),
                getSizeOrDefault()
        );
    }

}
