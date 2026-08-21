package com.oneenterprise.day5.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

/**
 * The handbook's Section 7 "simple learning example" wraps the User Service
 * call in a controller-level try/catch. It immediately says that's not the
 * preferred approach in a real application — this class is that preferred
 * approach: one place that decides what a downstream failure looks like to
 * OUR clients, instead of every controller repeating its own try/catch.
 *
 * RestClientException is the base type Spring's RestClient throws for
 * anything that goes wrong reaching User Service — connection refused,
 * timeout, DNS failure, or a non-2xx status code. All of those look the
 * same from Order Service's point of view for this exercise: User Service
 * could not give us a usable answer.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponse> handleUserServiceFailure(RestClientException ex) {
        ErrorResponse body = new ErrorResponse(
                "USER_SERVICE_UNAVAILABLE",
                "User service is unavailable. Please try again later.",
                HttpStatus.SERVICE_UNAVAILABLE.value());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        ErrorResponse body = new ErrorResponse(
                "INTERNAL_ERROR",
                "Something went wrong while processing the request.",
                HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
