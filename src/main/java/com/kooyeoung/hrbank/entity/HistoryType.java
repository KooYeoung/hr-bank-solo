package com.kooyeoung.hrbank.entity;

import com.kooyeoung.hrbank.exception.employeeHistory.InvalidHistoryTypeException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum HistoryType {
    CREATED, UPDATED, DELETED;

    private final static Set<String> AVAILABLE_TYPES = Arrays.stream(HistoryType.values())
            .map(Enum::name)
            .collect(Collectors.toUnmodifiableSet());

    public static HistoryType from(String type) {
        if (type == null || type.isBlank()) throw new InvalidHistoryTypeException("이력 타입이 존재하지 않습니다.");
        String normalizedType = type.trim().toUpperCase();
        if (!AVAILABLE_TYPES.contains(normalizedType))
            throw new InvalidHistoryTypeException("지원하지 않는 이력 타입입니다. type=" + type);

        return HistoryType.valueOf(normalizedType);
    }
}
