package com.oneenterprise.day5.userservice.dto;

/**
 * The handbook's reference DTO, as-is: a record with id, name, email.
 * This is the API contract — the one thing another service is allowed to
 * depend on. Nothing about how (or whether) the data is stored is visible
 * here.
 */
public record UserResponse(
        Long id,
        String name,
        String email
) {
}
