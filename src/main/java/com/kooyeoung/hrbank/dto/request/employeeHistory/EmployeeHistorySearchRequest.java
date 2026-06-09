package com.kooyeoung.hrbank.dto.request.employeeHistory;

import com.kooyeoung.hrbank.entity.HistoryType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

public record EmployeeHistorySearchRequest(
        String employeeNumber
        , String type
        , String memo
        , String ipAddress
        , LocalDateTime atFrom
        , LocalDateTime atTo
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

    private final static Set<String> SORT_FIELDS = Set.of("ipAddress" , "at");
    private final static Set<String> SORT_DIRECTIONS = Set.of("asc" , "desc");
    private final static Set<String> AVAILABLE_TYPES = Set.of(Arrays.toString(HistoryType.values()));

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

    public String getTypeOrDefault(){
        return AVAILABLE_TYPES.contains(type) ? type : "";
    }

}

