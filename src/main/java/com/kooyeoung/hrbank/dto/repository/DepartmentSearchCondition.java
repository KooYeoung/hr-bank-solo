package com.kooyeoung.hrbank.dto.repository;

public record DepartmentSearchCondition(
        String keyword
        ,String sortFiled
        ,String cursor
        ,long idAfter
        , boolean hasCursor
        , boolean isDesc
        , int size
) {

}
