package com.howtodoinjava.gateway;

import java.net.InetSocketAddress;
import java.time.Duration;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import reactor.core.publisher.Mono;

@Configuration
public class RouteConfig {

  @Bean
  public RouteLocator javaRoutes(RouteLocatorBuilder builder, RecipeServiceProperties recipeService) {
    return builder.routes()
        .route("flaky-recipes", r -> r.path("/api/flaky/**")
            .filters(f -> f
                .stripPrefix(1)                                  // /api/flaky/recipes -> /flaky/recipes
                .retry(retry -> retry
                    .setRetries(3)
                    .setStatuses(HttpStatus.SERVICE_UNAVAILABLE)
                    .setMethods(HttpMethod.GET)
                    .setBackoff(Duration.ofMillis(50), Duration.ofMillis(500), 2, false)))
            .uri(recipeService.uri()))
        .route("slow-recipes", r -> r.path("/api/slow/**")
            .filters(f -> f
                .stripPrefix(1)                                  // /api/slow/recipes -> /slow/recipes
                .circuitBreaker(cb -> cb
                    .setName("slowRecipes")
                    .setFallbackUri("forward:/fallback/recipes")))
            .uri(recipeService.uri()))
        .build();
  }

  @Bean
  public KeyResolver userKeyResolver() {
    return exchange -> {
      String user = exchange.getRequest().getHeaders().getFirst("X-User");
      if (user != null && !user.isBlank()) {
        return Mono.just(user.strip());                          // one bucket per user
      }
      InetSocketAddress remote = exchange.getRequest().getRemoteAddress();
      return Mono.just(remote != null ? remote.getHostString() : "anonymous");
    };
  }
}
