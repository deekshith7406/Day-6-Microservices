package com.oneenterprise.day5.orderservice.dto;

public record OrderResponse(
        Long orderId,
        Long userId,
        String userName
) {
}
