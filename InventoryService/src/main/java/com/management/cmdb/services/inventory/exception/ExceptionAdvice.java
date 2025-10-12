package com.management.cmdb.services.inventory.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

// Used toItemId change exception result, return a standardized ProblemDetail (RFC 7807 standard)
@RestControllerAdvice
public class ExceptionAdvice {

    private static final Logger lOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);

    private static ProblemDetail generateProblemDetail(HttpStatus httpStatus, Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, ex.getMessage());
        problemDetail.setProperties(Map.of(
                "timestamp", Instant.now().toString(),
                "uuid", UUID.randomUUID().toString()
        ));
        lOGGER.error("Exception caught: {}", problemDetail);
        return problemDetail;
    }

    /** Catch business exception from client **/
    @ExceptionHandler({
            BadRequestException.class,
            HttpClientErrorException.class,
    })
    public ResponseEntity<ProblemDetail> handleClientException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(generateProblemDetail(HttpStatus.BAD_REQUEST, ex));
    }

    /** Catch security exception **/
    @ExceptionHandler({
            HttpClientErrorException.Unauthorized.class,
    })
    public ResponseEntity<ProblemDetail> handleSecurityException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(generateProblemDetail(HttpStatus.UNAUTHORIZED, ex));
    }

    /** Catch all unmanaged exception from server **/
    @ExceptionHandler({
            HttpServerErrorException.class,
            Exception.class,
    })
    public ResponseEntity<ProblemDetail> handleGlobalException(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(generateProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }

}
