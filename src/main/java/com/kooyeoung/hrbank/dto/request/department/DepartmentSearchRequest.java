package com.kooyeoung.hrbank.dto.request.department;

import com.kooyeoung.hrbank.dto.repository.department.DepartmentSearchCondition;

import java.util.Set;

public record DepartmentSearchRequest(
        // 부서 이름 또는 설명
        String nameOrDescription
        // 이전 페이지 마지막 요소 ID
        , Long idAfter
        // 커서 (다음 페이지 시작점)
        , String cursor
        // 페이지 크기 (기본값: 10)
        , Integer size
        // 정렬 필드 (name 또는 establishedDate)
        , String sortField
        // 정렬 방향 (asc 또는 desc, 기본값: asc)
        , String sortDirection
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

        return !SORT_FIELDS.contains(normalizedSortField)
                ? DEFAULT_SORT_FIELD : normalizedSortField;
    }

    public String getSortDirectionOrDefault() {
        if (sortDirection == null || sortDirection.isBlank()) return DEFAULT_SORT_DIRECTION;

        String normalizedSortDirection = sortDirection.trim().toLowerCase();

        return !SORT_DIRECTIONS.contains(normalizedSortDirection)
                ? DEFAULT_SORT_DIRECTION : normalizedSortDirection;
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
