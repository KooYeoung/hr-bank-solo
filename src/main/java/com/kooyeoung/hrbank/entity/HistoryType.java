package com.kooyeoung.hrbank.entity;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum HistoryType {
    CREATED,UPDATED,DELETED;

    private final static Set<String> AVAILABLE_TYPES = Arrays.stream(HistoryType.values())
            .map(Enum::name)
            .collect(Collectors.toUnmodifiableSet());

    public static HistoryType from(String type){
        if(type == null || type.isBlank()) throw new IllegalArgumentException("이력타입이 존재하지 않습니다.");
        String normalizedType = type.trim().toUpperCase();
        if(!AVAILABLE_TYPES.contains(normalizedType)) throw new IllegalArgumentException("지원하지않는 이력타입입니다.");

        return HistoryType.valueOf(normalizedType);
    }
}
