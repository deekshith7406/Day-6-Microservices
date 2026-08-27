package com.oneenterprise.day5.orderservice.controller;

import com.oneenterprise.day5.orderservice.client.UserClient;
import com.oneenterprise.day5.orderservice.dto.OrderResponse;
import com.oneenterprise.day5.orderservice.dto.UserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
