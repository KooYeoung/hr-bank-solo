package com.kooyeoung.hrbank.dto.request.backupInfo;

import com.kooyeoung.hrbank.dto.repository.backupInfo.BackupInfoSearchCondition;
import com.kooyeoung.hrbank.entity.BackupStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

public record BackupInfoSearchRequest(
        // 부서 이름 또는 설명
        String worker
        , String status
        , LocalDateTime startAtFrom
        , LocalDateTime startAtTo
        // 이전 페이지 마지막 요소 ID
        , Long idAfter
        // 커서 (다음 페이지 시작점)
        , String cursor
        // 페이지 크기 (기본값: 10)
        , Integer size
        // 정렬 필드 (startedAt, endedAt, status)
        , String sortField
        // 정렬 방향 (asc 또는 desc, 기본값: desc)
        , String sortDirection
) {

    private final static Set<String> SORT_FIELDS = Set.of("startedAt" , "endedAt", "status");
    private final static Set<String> SORT_DIRECTIONS = Set.of("asc" , "desc");
    private final static Set<String> AVAILABLE_STATUS = Set.of(Arrays.toString(BackupStatus.values()));


    public int getSizeOrDefault(){
        return size == null || size <= 0  ? 10 : size;
    }

    public String getSortFieldOrDefault(){
        return sortField == null || sortField.isBlank() || !SORT_FIELDS.contains(sortField)
        ? "startedAt" : sortField;
    }

    public String getSortDirectionOrDefault(){
        return sortDirection == null || sortDirection.isBlank() || !SORT_DIRECTIONS.contains(sortDirection.toLowerCase())
                ? "desc" : sortDirection;
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

    public static BackupInfoSearchCondition from(BackupInfoSearchRequest request){
        return new BackupInfoSearchCondition(
                request.worker,
                request.status,
                request.startAtFrom,
                request.startAtTo,
                request.getSortFieldOrDefault(),
                request.cursor,
                request.idAfter,
                request.hasCursor(),
                request.isDesc(),
                request.getSizeOrDefault()
        );
    }
}
