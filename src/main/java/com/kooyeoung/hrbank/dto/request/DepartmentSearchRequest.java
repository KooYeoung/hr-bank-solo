package com.kooyeoung.hrbank.dto.request;

import java.util.Set;

public record DepartmentSearchRequest(
        // 부서 이름 또는 설명
        String nameOrDescription
        // 이전 페이지 마지막 요소 ID
        , Integer idAfter
        // 커서 (다음 페이지 시작점)
        , String cursor
        // 페이지 크기 (기본값: 10)
        , Integer size
        // 정렬 필드 (name 또는 establishedDate)
        , String sortField
        // 정렬 방향 (asc 또는 desc, 기본값: asc)
        , String sortDirection
) {

    private final static Set<String> sortFields = Set.of("name" , "establishedDate");
    private final static Set<String> sortDirections = Set.of("asc" , "desc");

    public int getSizeOrDefault(){
        return size == null || size <= 0  ? 10 : size;
    }

    public String getSortFiledOrDefault(){
        return sortField == null || sortField.isBlank() || !sortFields.contains(sortField)
        ? "name" : sortField;
    }

    public String getSortDirectionOrDefault(){
        return sortDirection == null || sortDirection.isBlank() || !sortDirections.contains(sortDirection.toLowerCase())
                ? "asc" : sortDirection;
    }

    public boolean isDesc(){
        return "desc".equalsIgnoreCase(getSortDirectionOrDefault());
    }

    public boolean hasCursor(){
        return idAfter !=null && cursor !=null && !cursor.isBlank();
    }

}
