package com.oneenterprise.day5.orderservice.exception;

import java.time.Instant;

public record ErrorResponse(
        String error,
        String message,
        int status,
        Instant timestamp
) {
    public ErrorResponse(String error, String message, int status) {
        this(error, message, status, Instant.now());
    }
}
