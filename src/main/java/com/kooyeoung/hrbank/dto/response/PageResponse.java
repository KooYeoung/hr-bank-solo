package com.kooyeoung.hrbank.dto.response;

import java.util.List;

public record PageResponse <T>(
        List<T> content
        , String nextCursor
        , Long nextIdAfter
        , int size
        , long totalElements
        , boolean hasNext
){
}
