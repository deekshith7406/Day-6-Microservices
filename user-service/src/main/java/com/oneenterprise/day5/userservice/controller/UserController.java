package com.oneenterprise.day5.userservice.controller;

import com.oneenterprise.day5.userservice.dto.UserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * GET /users/{id}
 *
 * The handbook's own reference implementation always returns the same
 * hardcoded user ("John") regardless of id, to keep the very first version
 * as simple as possible. This version keeps that same behavior for id
 * 1001 specifically (so the handbook's example curl/test still matches
 * exactly), but adds one more seeded user so the "build it yourself"
 * challenge has more than one real record to work with.
 *
 * Try it:
 *   GET http://localhost:8081/users/1001  -> John
 *   GET http://localhost:8081/users/1002  -> Priya
 *   GET http://localhost:8081/users/9999  -> falls back to the 1001 record,
 *       matching the handbook's original "always returns a user" behavior
 *       (Day 5 doesn't ask for a not-found case here — Order Service's own
 *       error handling is the focus of today's exercise instead).
 */
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
