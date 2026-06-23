package com.kooyeoung.hrbank.exception;

import java.time.LocalDateTime;

public record ErrorResponse (
        LocalDateTime timestamp,
        Integer status,
        String message,
        String details
) {

    public static ErrorResponse from(Integer statusCode,String message, String details){
        return new ErrorResponse(LocalDateTime.now(), statusCode, message, details);
    }

}
