package com.kooyeoung.hrbank.dto.response;

public record DiffDto(
        String propertyName
        ,String before
        ,String after
) {
}
