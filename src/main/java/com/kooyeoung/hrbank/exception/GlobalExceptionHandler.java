package com.kooyeoung.hrbank.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomBadRequestException.class)
    public ResponseEntity<ErrorResponse> badRequestHandler(CustomBadRequestException e){
        log.warn(e.getMessage());

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(badRequest)
                .body(ErrorResponse.from(badRequest.value(), badRequest.name(), e.getMessage()));
    }


    @ExceptionHandler(CustomNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundHandler(CustomNotFoundException e) {
        log.warn(e.getMessage());

        HttpStatus notFound = HttpStatus.NOT_FOUND;

        return ResponseEntity
                .status(notFound)
                .body(ErrorResponse.from(notFound.value(), notFound.name(), e.getMessage()));
    }

    @ExceptionHandler(CustomInternalServerException.class)
    public ResponseEntity<ErrorResponse> handleInternalServer(CustomInternalServerException e){
        log.error("internal server error: {}", e.getMessage(), e);

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(httpStatus)
                .body(ErrorResponse.from(httpStatus.value(), httpStatus.name(), e.getMessage()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
        log.error("Unhandled exception", e);

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(httpStatus)
                .body(ErrorResponse.from(httpStatus.value(), httpStatus.name() ,"알 수 없는 오류가 발생했습니다."));

    }

}
