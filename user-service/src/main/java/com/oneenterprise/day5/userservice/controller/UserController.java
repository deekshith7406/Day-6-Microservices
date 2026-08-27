package com.oneenterprise.day5.userservice.controller;

import com.oneenterprise.day5.userservice.dto.UserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Map<Long, UserResponse> USERS = Map.of(
            1001L, new UserResponse(1001L, "John", "john@example.com"),
            1002L, new UserResponse(1002L, "Priya", "priya@example.com")
    );

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return USERS.getOrDefault(id, USERS.get(1001L));
    }
}
