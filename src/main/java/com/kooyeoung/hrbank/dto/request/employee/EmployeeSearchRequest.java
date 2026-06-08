package com.kooyeoung.hrbank.dto.request.employee;

import com.kooyeoung.hrbank.entity.EmployeeStatus;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;

public record EmployeeSearchRequest(
        String nameOrEmail
        , String employeeNumber
        , String departmentName
        , String position
        , LocalDate hireDateFrom
        , LocalDate hireDateTo
        , String status
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

    private final static Set<String> SORT_FIELDS = Set.of("name" , "employeeNumber", "hireDate");
    private final static Set<String> SORT_DIRECTIONS = Set.of("asc" , "desc");
    private final static Set<String> AVAILABLE_STATUS = Set.of(Arrays.toString(EmployeeStatus.values()));

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

    public String getStatusOrDefault(){
        return AVAILABLE_STATUS.contains(status) ? status : "";
    }

}
