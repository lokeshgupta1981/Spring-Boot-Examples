package com.howtodoinjava.gateway;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

  @GetMapping("/fallback/recipes")
  public Mono<ResponseEntity<Map<String, String>>> recipesFallback() {
    return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(Map.of("message", "Recipe service is slow, try again later")));
  }
}
