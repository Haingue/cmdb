package com.management.cmdb.inventory.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;

// Used to change exception result, return a standardized ProblemDetail (RFC 7807 standard)
@RestControllerAdvice
public class ExceptionAdvice {

    /** Catch business exception from client **/
    @ExceptionHandler({
            BadRequestException.class,
            HttpClientErrorException.class,
    })
    public ResponseEntity<Object> handleClientException(RuntimeException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(
                ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage())
        );
    }

    /** Catch security exception **/
    @ExceptionHandler({
            HttpClientErrorException.Unauthorized.class,
    })
    public ResponseEntity<Object> handleSecurityException(RuntimeException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage())
        );
    }

    /** Catch all unmanaged exception from server **/
    @ExceptionHandler({
            HttpServerErrorException.class,
            Exception.class,
    })
    public ResponseEntity<ProblemDetail> handleGlobalException(RuntimeException ex, WebRequest request) {
        return ResponseEntity.internalServerError().body(
                ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage())
        );
    }

}
