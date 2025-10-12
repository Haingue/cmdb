package com.management.cmdb.backend.configuration;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger lOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static ProblemDetail generateProblemDetail(HttpStatus httpStatus, Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, ex.getMessage());
        problemDetail.setProperties(Map.of(
                "timestamp", Instant.now().toString(),
                "uuid", UUID.randomUUID().toString()
        ));
        lOGGER.error("Exception caught: {}", problemDetail);
        return problemDetail;
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ProblemDetail> handleFeignException(FeignException e) {
        return ResponseEntity.badRequest().body(generateProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, e));
    }

}
