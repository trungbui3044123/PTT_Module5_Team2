package com.module5.team2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(BusinessException.class)
//    public ResponseEntity<Object> handleBusinessException(
//            BusinessException ex, WebRequest request) {
//        log.error("Business exception: {}", ex.getMessage());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(HttpStatus.BAD_REQUEST.value()+" : "+ex.getMessage());
//    }

    
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<Object> handleResourceNotFoundException(
//            ResourceNotFoundException ex, WebRequest request) {
//        log.error("Resource not found: {}", ex.getMessage());
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(HttpStatus.NOT_FOUND.value()+" :"+ ex.getMessage());
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", 400);
        response.put("error", "VALIDATION_ERROR");
        response.put("message", "Dữ liệu không hợp lệ");
        response.put("details", errors);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException ex) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", 400);
        response.put("error", "BUSINESS_ERROR");
        response.put("message", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.badRequest().body(response);
    }


@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("status", 404);
    response.put("error", "NOT_FOUND");
    response.put("message", ex.getMessage());
    response.put("timestamp", LocalDateTime.now());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
}
}
