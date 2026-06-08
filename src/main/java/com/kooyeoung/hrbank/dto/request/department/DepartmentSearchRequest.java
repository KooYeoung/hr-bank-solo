package com.kooyeoung.hrbank.dto.request.department;

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

    private final static Set<String> SORT_FIELDS = Set.of("name" , "establishedDate");
    private final static Set<String> SORT_DIRECTIONS = Set.of("asc" , "desc");

    public int getSizeOrDefault(){
        return size == null || size <= 0  ? 10 : size;
    }

    public String getSortFieldOrDefault(){
        return sortField == null || sortField.isBlank() || !SORT_FIELDS.contains(sortField)
        ? "name" : sortField;
    }

    public String getSortDirectionOrDefault(){
        return sortDirection == null || sortDirection.isBlank() || !SORT_DIRECTIONS.contains(sortDirection.toLowerCase())
                ? "asc" : sortDirection;
    }

    public boolean isDesc(){
        return "desc".equalsIgnoreCase(getSortDirectionOrDefault());
    }

    public boolean hasCursor(){
        return idAfter !=null && cursor !=null && !cursor.isBlank();
    }

}
