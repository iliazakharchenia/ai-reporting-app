package com.ai.reporting.web;

import com.ai.reporting.core.exception.CredentialsIncorrectException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice(basePackages = "com.ai.reporting.web.reporting")
public class ReportingExceptionHandler {

    @ExceptionHandler(CredentialsIncorrectException.class)
    public Mono<ResponseEntity<String>> handleUserNotFoundException(CredentialsIncorrectException ex) {
        return Mono.just(ResponseEntity.status(401).body("Credentials incorrect!"));
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<String>> handleGenericException(RuntimeException ex) {
        return Mono.just(ResponseEntity.status(400).body("Something went wrong!"));
    }

}
