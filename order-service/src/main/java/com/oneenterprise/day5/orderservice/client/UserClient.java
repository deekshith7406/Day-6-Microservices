package com.oneenterprise.day5.orderservice.client;

import com.oneenterprise.day5.orderservice.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class UserClient {

    private final RestClient restClient;

    public UserClient(RestClient userRestClient) {
        this.restClient = userRestClient;
    }

    public UserResponse getUser(Long id) {
        return restClient.get()
                .uri("/users/{id}", id)
                .retrieve()
                .body(UserResponse.class);
    }
}
