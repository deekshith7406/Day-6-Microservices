package com.oneenterprise.day5.orderservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Section 5 of the handbook shows the base URL hard-coded directly in this
 * bean, for simplicity. Section 11 asks you to fix that — this version
 * reads it from configuration instead (application.properties:
 * user.service.base-url), so local/test/staging/prod can point at
 * different addresses without a Java change or rebuild.
 */
@Configuration
public class RestClientConfig {

    @Bean
    RestClient userRestClient(@Value("${user.service.base-url}") String userServiceBaseUrl) {
        return RestClient.builder()
                .baseUrl(userServiceBaseUrl)
                .build();
    }
}
