package com.oneenterprise.day5.orderservice.client;

import com.oneenterprise.day5.orderservice.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * The one place in Order Service that knows how to call User Service.
 * Deliberately thin, and deliberately has NO try/catch — per the handbook's
 * own advice, failure handling belongs in one centralized place
 * (see exception/GlobalExceptionHandler), not scattered across every class
 * that happens to make an HTTP call.
 *
 * Read top to bottom:
 *   get()      - prepare a GET request
 *   uri()      - the downstream endpoint, with a path variable
 *   retrieve() - actually send the request
 *   body()     - deserialize the JSON response into UserResponse
 */
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
