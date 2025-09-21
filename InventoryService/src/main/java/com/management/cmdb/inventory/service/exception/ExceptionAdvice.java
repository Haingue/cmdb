package com.management.cmdb.inventory.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

// Used to change exception result, return a standardized ProblemDetail (RFC 7807 standard)
@RestControllerAdvice
public class ExceptionAdvice {

    private static ProblemDetail generateProblemDetail(HttpStatus httpStatus, RuntimeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, ex.getMessage());
        problemDetail.setProperties(Map.of(
                "timestamp", Instant.now().toString(),
                "uuid", UUID.randomUUID().toString()
        ));
        return problemDetail;
    }

    /** Catch business exception from client **/
    @ExceptionHandler({
            BadRequestException.class,
            HttpClientErrorException.class,
    })
    public ResponseEntity<Object> handleClientException(RuntimeException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(generateProblemDetail(HttpStatus.BAD_REQUEST, ex));
    }

    /** Catch security exception **/
    @ExceptionHandler({
            HttpClientErrorException.Unauthorized.class,
    })
    public ResponseEntity<Object> handleSecurityException(RuntimeException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(generateProblemDetail(HttpStatus.UNAUTHORIZED, ex));
    }

    /** Catch all unmanaged exception from server **/
    @ExceptionHandler({
            HttpServerErrorException.class,
            Exception.class,
    })
    public ResponseEntity<ProblemDetail> handleGlobalException(RuntimeException ex, WebRequest request) {
        return ResponseEntity.internalServerError()
                .body(generateProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }

}
