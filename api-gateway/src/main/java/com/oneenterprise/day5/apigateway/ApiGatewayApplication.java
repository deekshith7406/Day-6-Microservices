package com.oneenterprise.day5.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * The single client-facing entry point (Day 6). Owns no business logic —
 * it discovers USER-SERVICE and ORDER-SERVICE by name via Eureka and
 * routes to them. See src/main/resources/application.yml for the actual
 * routing table.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
