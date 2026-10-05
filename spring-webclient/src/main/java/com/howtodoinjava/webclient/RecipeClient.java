package com.howtodoinjava.webclient;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Calls the recipes API with a WebClient. Every method returns a Mono or a Flux;
 * nothing is sent until someone subscribes.
 */
@Service
public class RecipeClient {

  private final WebClient webClient;

  public RecipeClient(WebClient.Builder builder,
      @Value("${recipes.base-url:http://localhost:8080}") String baseUrl) {
    this.webClient = builder
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.USER_AGENT, "recipe-client")
        .build();
  }

  /** GET /recipes/{id}, the response body as one object. */
  public Mono<Recipe> findById(long id) {
    return webClient.get()
        .uri("/recipes/{id}", id)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError,
            response -> Mono.error(new RecipeNotFoundException(id)))
        .bodyToMono(Recipe.class);
  }

  /** GET /recipes?maxMinutes=30, the JSON array as a Flux of objects. */
  public Flux<Recipe> findQuick(int maxMinutes) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/recipes")
            .queryParam("maxMinutes", maxMinutes)
            .build())
        .retrieve()
        .bodyToFlux(Recipe.class);
  }

  /** GET /recipes, the whole array as a List. */
  public Mono<List<Recipe>> findAll() {
    return webClient.get()
        .uri("/recipes")
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<List<Recipe>>() {});
  }

  /** POST /recipes with a JSON body, the full response with status and headers. */
  public Mono<ResponseEntity<Recipe>> create(Recipe recipe) {
    return webClient.post()
        .uri("/recipes")
        .header("X-Request-Source", "batch-import")
        .bodyValue(recipe)
        .retrieve()
        .toEntity(Recipe.class);
  }

  /** DELETE /recipes/{id}, no response body expected. */
  public Mono<Void> delete(long id) {
    return webClient.delete()
        .uri("/recipes/{id}", id)
        .retrieve()
        .toBodilessEntity()
        .then();
  }

  /** GET /recipes/{id} with exchangeToMono(), which decides per status code. */
  public Mono<Recipe> findOrDefault(long id, Recipe fallback) {
    return webClient.get()
        .uri("/recipes/{id}", id)
        .exchangeToMono(response -> {
          if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(Recipe.class);
          }
          if (response.statusCode().value() == 404) {
            return response.releaseBody().thenReturn(fallback);
          }
          return response.createError();
        });
  }

  /** Blocking call for code that is not reactive, with a fallback when the server answers 5xx. */
  public Recipe findByIdBlocking(long id) {
    try {
      return findById(id).block();
    }
    catch (WebClientResponseException e) {
      throw new IllegalStateException("Server answered " + e.getStatusCode(), e);
    }
  }
}
