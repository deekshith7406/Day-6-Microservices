package com.oneenterprise.day5.discoveryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * The service registry — a "phone directory" for the platform. Other
 * services register themselves here on startup; the API Gateway (and,
 * later, any service) asks this registry "where is USER-SERVICE right
 * now?" instead of relying on a fixed, hard-coded address.
 *
 * This process owns no business logic and doesn't register with itself —
 * see application.properties (register-with-eureka=false, fetch-registry=false).
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);
    }
}
