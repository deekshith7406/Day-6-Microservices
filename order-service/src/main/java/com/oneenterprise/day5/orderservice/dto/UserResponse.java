package com.oneenterprise.day5.orderservice.dto;

/**
 * Order Service's own view of what User Service returns. Same fields as
 * User Service's UserResponse, but deliberately a separate class — Order
 * Service does not import User Service's module or share a JAR with it.
 * Each service owns its side of the contract, even when the shapes match.
 */
public record UserResponse(
        Long id,
        String name,
        String email
) {
}
