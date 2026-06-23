package com.kooyeoung.hrbank.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e){

        String errorDetails = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> field.getField() + " : " + field.getDefaultMessage()
                ).collect(Collectors.joining(", "));

        log.warn("validation errors={}", errorDetails);

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        return ResponseEntity
                .status(badRequest)
                .body(ErrorResponse.from(badRequest.value(), "요청 본문에 일부 필드가 유효하지 않습니다.", errorDetails));

    }

    @ExceptionHandler(CustomBadRequestException.class)
    public ResponseEntity<ErrorResponse> badRequestHandler(CustomBadRequestException e){
        log.warn(e.getMessage());

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(badRequest)
                .body(ErrorResponse.from(badRequest.value(), "잘못된 요청입니다.", e.getMessage()));
    }


    @ExceptionHandler(CustomNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundHandler(CustomNotFoundException e) {
        log.warn(e.getMessage());

        HttpStatus notFound = HttpStatus.NOT_FOUND;

        return ResponseEntity
                .status(notFound)
                .body(ErrorResponse.from(notFound.value(), "요청한 리소스를 찾을 수 없습니다.", e.getMessage()));
    }

    @ExceptionHandler(CustomInternalServerException.class)
    public ResponseEntity<ErrorResponse> handleInternalServer(CustomInternalServerException e){
        log.error("internal server error: {}", e.getMessage(), e);

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(httpStatus)
                .body(ErrorResponse.from(httpStatus.value(),  "서버 내부 오류가 발생했습니다.", e.getMessage()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
        log.error("Unhandled exception", e);

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(httpStatus)
                .body(ErrorResponse.from(httpStatus.value(),  "서버 내부 오류가 발생했습니다.","알 수 없는 오류가 발생했습니다."));

    }

}
