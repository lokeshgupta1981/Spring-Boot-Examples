package com.howtodoinjava.gateway;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@Configuration
public class ResilienceConfig {

  @Bean
  public Customizer<ReactiveResilience4JCircuitBreakerFactory> slowRecipesCustomizer() {
    return factory -> factory.configure(builder -> builder
        .timeLimiterConfig(TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(2))              // give up after 2 s
            .build())
        .circuitBreakerConfig(CircuitBreakerConfig.custom()
            .slidingWindowSize(4)                                // look at the last 4 calls
            .minimumNumberOfCalls(4)
            .failureRateThreshold(50)                            // open at 50% failures
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .build()), "slowRecipes");
  }
}
