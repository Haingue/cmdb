package com.management.cmdb.backend.configuration;

import com.management.cmdb.backend.scripting.ScriptException;
import com.management.cmdb.core.models.exceptions.CoreException;
import com.management.cmdb.core.models.exceptions.InvalidObjectException;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static ProblemDetail generateProblemDetail(HttpStatus httpStatus, Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, ex.getMessage());
        problemDetail.setProperties(Map.of(
                "timestamp", Instant.now().toString(),
                "uuid", UUID.randomUUID().toString()
        ));
        LOGGER.error("Exception caught: {}", problemDetail);
        return problemDetail;
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ProblemDetail> handleFeignException(FeignException e) {
        return ResponseEntity.badRequest().body(generateProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, e));
    }

    @ExceptionHandler(ScriptException.class)
    public ResponseEntity<ProblemDetail> handleScriptException(ScriptException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Syntax Error, correct your script");
        problemDetail.setProperties(Map.of(
                "script-error", e.getMessage(),
                "timestamp", Instant.now().toString(),
                "uuid", UUID.randomUUID().toString()
        ));
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler({ InvalidObjectException.class, CoreException.class })
    public ResponseEntity<ProblemDetail> handleBusinessException(InvalidObjectException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Malformed object, check your input");
        problemDetail.setProperties(Map.of(
                "reason", e.getMessage(),
                "timestamp", Instant.now().toString(),
                "uuid", UUID.randomUUID().toString()
        ));
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler({ Exception.class, RuntimeException.class })
    public ResponseEntity<ProblemDetail> handleException(Exception e) {
        LOGGER.error("Error caught", e);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        problemDetail.setProperties(Map.of(
                "timestamp", Instant.now().toString(),
                "uuid", UUID.randomUUID().toString()
        ));
        return ResponseEntity.badRequest().body(problemDetail);
    }

}
