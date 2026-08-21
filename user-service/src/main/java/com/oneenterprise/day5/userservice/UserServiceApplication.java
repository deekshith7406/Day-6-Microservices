package com.oneenterprise.day5.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Day 6: @EnableDiscoveryClient makes the registration behavior explicit in
 * code (Spring Cloud actually auto-configures this once the Eureka client
 * dependency is on the classpath, but leaving the annotation in makes it
 * obvious at a glance that this service participates in discovery).
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
