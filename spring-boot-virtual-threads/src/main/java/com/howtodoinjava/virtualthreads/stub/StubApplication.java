package com.howtodoinjava.virtualthreads.stub;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Plays the two slow downstream services: GET /forecast/{city} and GET /air-quality/{city}.
 * Runs on WebFlux and Netty so that the stub itself is never the bottleneck in a load test.
 * Uses application.properties plus application-stub.properties (port 9090, delay 250 ms).
 */
@SpringBootApplication
public class StubApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(StubApplication.class)
        .web(WebApplicationType.REACTIVE)
        .profiles("stub")
        .run(args);
  }
}
