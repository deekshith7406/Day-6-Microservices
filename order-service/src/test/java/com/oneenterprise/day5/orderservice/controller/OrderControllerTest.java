package com.oneenterprise.day5.orderservice.controller;

import com.oneenterprise.day5.orderservice.client.UserClient;
import com.oneenterprise.day5.orderservice.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.ResourceAccessException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Day 5 explicitly asks for "a successful end-to-end test" and "a test for
 * User Service being unavailable". A true end-to-end test needs both real
 * processes running (see README.md for the curl-based walkthrough) — these
 * are the automated equivalent: UserClient is mocked so each test controls
 * exactly what "User Service" does, without needing a second JVM running
 * during the build.
 */
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Test
    void returnsOrderWithUserDetails_whenUserServiceRespondsSuccessfully() throws Exception {
        when(userClient.getUser(eq(1001L)))
                .thenReturn(new UserResponse(1001L, "John", "john@example.com"));

        mockMvc.perform(get("/orders/5001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(5001))
                .andExpect(jsonPath("$.userId").value(1001))
                .andExpect(jsonPath("$.userName").value("John"));
    }

    @Test
    void returns503_whenUserServiceIsUnavailable() throws Exception {
        when(userClient.getUser(eq(1001L)))
                .thenThrow(new ResourceAccessException("Connection refused"));

        mockMvc.perform(get("/orders/5001"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("USER_SERVICE_UNAVAILABLE"));
    }
}
