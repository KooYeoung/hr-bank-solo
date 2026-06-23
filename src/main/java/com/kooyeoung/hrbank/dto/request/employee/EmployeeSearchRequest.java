package com.kooyeoung.hrbank.dto.request.employee;

import com.kooyeoung.hrbank.dto.repository.employee.EmployeeSearchCondition;
import com.kooyeoung.hrbank.entity.EmployeeStatus;

import java.time.LocalDate;
import java.util.Set;

public record EmployeeSearchRequest(
        String nameOrEmail,
        String employeeNumber,
        String departmentName,
        String position,
        LocalDate hireDateFrom,
        LocalDate hireDateTo,
        String status,
        Long idAfter,
        String cursor,
        Integer size,
        String sortField,
        String sortDirection
) {

    private final static String DEFAULT_SORT_FILED = "name";
    private final static String DEFAULT_SORT_DIRECTION = "asc";
    private final static Set<String> SORT_FIELDS = Set.of(DEFAULT_SORT_FILED, "employeeNumber", "hireDate");
    private final static Set<String> SORT_DIRECTIONS = Set.of(DEFAULT_SORT_DIRECTION, "desc");

    public int getSizeOrDefault() {
        return size == null || size <= 0 ? 10 : size;
    }

    public String getSortFieldOrDefault() {
        if (sortField == null || sortField.isBlank()) return DEFAULT_SORT_FILED;

        String normalizedSortField = sortField.trim();

        return !SORT_FIELDS.contains(normalizedSortField) ? DEFAULT_SORT_FILED : normalizedSortField;
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

    public EmployeeStatus getStatusOrDefault() {
        if (status == null || status.isBlank()) return null;

        return EmployeeStatus.from(status);
    }

    public EmployeeSearchCondition toCondition() {
        return new EmployeeSearchCondition(
                nameOrEmail,
                employeeNumber,
                departmentName,
                position,
                hireDateFrom,
                hireDateTo,
                getStatusOrDefault(),
                getSortFieldOrDefault(),
                cursor,
                idAfter,
                hasCursor(),
                isDesc(),
                getSizeOrDefault()
        );
    }

}
