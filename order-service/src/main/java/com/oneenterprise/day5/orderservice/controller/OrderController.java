package com.oneenterprise.day5.orderservice.controller;

import com.oneenterprise.day5.orderservice.client.UserClient;
import com.oneenterprise.day5.orderservice.dto.OrderResponse;
import com.oneenterprise.day5.orderservice.dto.UserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API CONTRACT
 * ------------
 * GET /orders/{orderId}
 *   200 OK                  -> OrderResponse (includes the user this order belongs to)
 *   503 Service Unavailable -> ErrorResponse, error = "USER_SERVICE_UNAVAILABLE"
 *                              (see exception/GlobalExceptionHandler)
 *
 * No try/catch here on purpose — see GlobalExceptionHandler for why.
 *
 * Like the handbook's own reference implementation, this always asks for
 * user 1001 regardless of orderId — today's focus is the mechanics of the
 * call itself, not order-to-user data modeling (that's what the full
 * `one-enterprise-platform` project's Order model already does).
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final UserClient userClient;

    public OrderController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable Long orderId) {
        UserResponse user = userClient.getUser(1001L);
        return new OrderResponse(orderId, user.id(), user.name());
    }
}
