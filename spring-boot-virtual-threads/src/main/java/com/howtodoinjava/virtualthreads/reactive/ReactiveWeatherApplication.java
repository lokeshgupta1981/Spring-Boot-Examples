package com.howtodoinjava.virtualthreads.reactive;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * The same weather API on Spring WebFlux and Netty.
 * Uses application.properties plus application-reactive.properties.
 */
@SpringBootApplication
public class ReactiveWeatherApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(ReactiveWeatherApplication.class)
        .web(WebApplicationType.REACTIVE)
        .profiles("reactive")
        .run(args);
  }
}
