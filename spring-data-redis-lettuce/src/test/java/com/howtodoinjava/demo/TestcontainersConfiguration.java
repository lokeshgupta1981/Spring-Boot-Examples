package com.howtodoinjava.demo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;

/**
 * Starts a Redis container as a Spring bean. ServiceConnection hands the container's
 * host and port to Spring Boot, so the tests need no spring.data.redis.host or port.
 * The container stops when the test application context closes.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

  @Bean
  @ServiceConnection(name = "redis")
  GenericContainer<?> redisContainer() {
    return new GenericContainer<>("redis:8.2").withExposedPorts(6379);
  }
}
