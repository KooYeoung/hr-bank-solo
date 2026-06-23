package com.kooyeoung.hrbank.dto.request.employeeHistory;

import com.kooyeoung.hrbank.dto.repository.employeeHistory.EmployeeHistorySearchCondition;
import com.kooyeoung.hrbank.entity.HistoryType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

public record EmployeeHistorySearchRequest(
        String employeeNumber,
        String type,
        String memo,
        String ipAddress,
        LocalDateTime atFrom,
        LocalDateTime atTo,
        Long idAfter,
        String cursor,
        Integer size,
        String sortField,
        String sortDirection
) {

    private final static String DEFAULT_SORT_FIELD = "at";
    private final static String DEFAULT_SORT_DIRECTION = "desc";
    private final static Set<String> SORT_FIELDS = Set.of("ipAddress" , DEFAULT_SORT_FIELD);
    private final static Set<String> SORT_DIRECTIONS = Set.of(DEFAULT_SORT_DIRECTION, "asc");

    public int getSizeOrDefault(){
        return size == null || size <= 0  ? 10 : size;
    }

    public String getSortFieldOrDefault(){
        if(sortField == null || sortField.isBlank()) return DEFAULT_SORT_FIELD;

        String normalizedSortField = sortField.trim();

        return !SORT_FIELDS.contains(normalizedSortField)
                ? DEFAULT_SORT_FIELD : normalizedSortField;
    }

    public String getSortDirectionOrDefault(){
        if(sortDirection == null || sortDirection.isBlank()) return DEFAULT_SORT_DIRECTION;

        String normalizedSortDirection = sortDirection.trim().toLowerCase();

        return !SORT_DIRECTIONS.contains(normalizedSortDirection)
                ? DEFAULT_SORT_DIRECTION: normalizedSortDirection;
    }

    public boolean isDesc(){
        return "desc".equalsIgnoreCase(getSortDirectionOrDefault());
    }

    public boolean hasCursor(){
        return idAfter !=null && cursor !=null && !cursor.isBlank();
    }

    public HistoryType getTypeOrDefault(){
        if(type == null || type.isBlank()) return null;

        return HistoryType.from(type);
    }

    public EmployeeHistorySearchCondition toCondition(){
        return new EmployeeHistorySearchCondition(
                employeeNumber,
                getTypeOrDefault(),
                memo,
                ipAddress,
                atFrom,
                atTo,
                getSortFieldOrDefault(),
                cursor,
                idAfter,
                hasCursor(),
                isDesc(),
                getSizeOrDefault()
        );
    }

}

