package com.kooyeoung.hrbank.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ChangeLogDetailDto(
        Long id
        , String type
        , String employeeNumber
        , String memo
        , String ipAddress
        , LocalDateTime at
        , String employeeName
        , Long profileImageId
        , List<DiffDto> diffs
        ) {

        public static ChangeLogDetailDto fromRows(List<ChangeLogDetailRowDto> rows) {
                if (rows == null || rows.isEmpty()) {
                        throw new IllegalArgumentException("직원 이력이 존재하지 않습니다.");
                }

                ChangeLogDetailRowDto first = rows.get(0);

                List<DiffDto> diffs = rows.stream()
                        .filter(row -> row.property() != null)
                        .map(row -> new DiffDto(
                                row.property().name(),
                                row.beforeValue(),
                                row.afterValue()
                        ))
                        .toList();

                return new ChangeLogDetailDto(
                        first.id(),
                        first.type().toString(),
                        first.employeeNumber(),
                        first.memo(),
                        first.ipAddress(),
                        first.at(),
                        first.employeeName(),
                        first.profileImageId(),
                        diffs
                );
        }
}
