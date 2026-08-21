package com.oneenterprise.day5.orderservice.dto;

/**
 * API CONTRACT — GET /orders/{orderId} (200 response body)
 *
 * The handbook's own minimal example returns a plain String
 * ("Order 5001 belongs to John"). This returns the same information as
 * structured JSON instead — a small nod to Day 2's "intentional response
 * model" lesson, since a real consumer would rather parse fields than a
 * sentence.
 */
public record OrderResponse(
        Long orderId,
        Long userId,
        String userName
) {
}
