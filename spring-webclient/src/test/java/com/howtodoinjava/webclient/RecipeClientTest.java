package com.howtodoinjava.webclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Starts the app on a random port and calls the recipes API through RecipeClient.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecipeClientTest {

  @LocalServerPort
  int port;

  @Autowired
  WebClient.Builder builder;

  RecipeClient client;

  @BeforeEach
  void setUp() {
    client = new RecipeClient(builder, "http://localhost:" + port);
  }

  @Test
  void createClientAndGetOneRecipe() {
    WebClient webClient = WebClient.create("http://localhost:" + port);

    Mono<Recipe> mono = webClient.get()
        .uri("/recipes/{id}", 1)
        .retrieve()
        .bodyToMono(Recipe.class);     // nothing is sent yet

    Recipe recipe = mono.block();      // sends the request and waits
    System.out.println("WebClient.create -> " + recipe);

    assertThat(recipe.id()).isEqualTo(1L);
  }

  @Test
  void notFoundWithoutOnStatus() {
    WebClient webClient = WebClient.create("http://localhost:" + port);

    assertThatThrownBy(() -> webClient.get().uri("/recipes/{id}", 999)
            .retrieve().bodyToMono(Recipe.class).block())
        .satisfies(e -> System.out.println("default 404 -> " + e.getClass().getName() + ": " + e.getMessage()));
  }

  @Test
  void getOneRecipe() {
    Recipe recipe = client.findById(1).block();   // Recipe[id=1, name=Pancakes, minutes=20]
    System.out.println("GET /recipes/1 -> " + recipe);

    assertThat(recipe.name()).isEqualTo("Pancakes");
  }

  @Test
  void getListAsFluxAndAsList() {
    List<Recipe> quick = client.findQuick(30).collectList().block();
    System.out.println("GET /recipes?maxMinutes=30 -> " + quick);

    List<Recipe> all = client.findAll().block();
    System.out.println("GET /recipes -> " + all.size() + " recipes");

    assertThat(quick).extracting(Recipe::name).containsExactly("Pancakes", "Omelette");
    assertThat(all).hasSizeGreaterThanOrEqualTo(3);
  }

  @Test
  void postCreatesRecipe() {
    ResponseEntity<Recipe> response = client.create(new Recipe(null, "Salad", 5)).block();
    System.out.println("POST /recipes -> " + response.getStatusCode()
        + " Location=" + response.getHeaders().getLocation()
        + " body=" + response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody().id()).isNotNull();
  }

  @Test
  void deleteReturnsNoBody() {
    Recipe created = client.create(new Recipe(null, "Toast", 3)).block().getBody();

    client.delete(created.id()).block();
    System.out.println("DELETE /recipes/" + created.id() + " -> done");

    assertThatThrownBy(() -> client.findById(created.id()).block())
        .isInstanceOf(RecipeNotFoundException.class);
  }

  @Test
  void onStatusMapsNotFoundToOurException() {
    Mono<Recipe> missing = client.findById(999);

    StepVerifier.create(missing)
        .expectErrorMessage("Recipe 999 not found")
        .verify();
    System.out.println("GET /recipes/999 -> RecipeNotFoundException: Recipe 999 not found");
  }

  @Test
  void exchangeToMonoReturnsFallbackOn404() {
    Recipe fallback = new Recipe(0L, "none", 0);

    Recipe found = client.findOrDefault(1, fallback).block();
    Recipe defaulted = client.findOrDefault(999, fallback).block();
    System.out.println("exchangeToMono 1 -> " + found);
    System.out.println("exchangeToMono 999 -> " + defaulted);

    assertThat(defaulted).isEqualTo(fallback);
  }

  @Test
  void readTimeoutFromPropertiesStopsSlowCall() throws Exception {
    try (okhttp3.mockwebserver.MockWebServer slow = new okhttp3.mockwebserver.MockWebServer()) {
      slow.start();
      slow.enqueue(new okhttp3.mockwebserver.MockResponse()
          .setHeader("Content-Type", "application/json")
          .setBody("{\"id\":1,\"name\":\"Pancakes\",\"minutes\":20}")
          .setBodyDelay(5, java.util.concurrent.TimeUnit.SECONDS));
      RecipeClient slowClient = new RecipeClient(builder, slow.url("/").toString());

      assertThatThrownBy(() -> slowClient.findById(1).block())
          .satisfies(e -> System.out.println("read-timeout=3s -> " + e.getClass().getSimpleName() + ": " + e.getMessage()));
    }
  }

  @Test
  void blockOnEventLoopThreadIsRejected() {
    // map() runs on the Reactor Netty thread that received the response, where block() is not allowed
    Mono<Recipe> nested = client.findById(1).map(first -> client.findById(2).block());

    assertThatThrownBy(nested::block)
        .isInstanceOf(IllegalStateException.class)
        .satisfies(e -> System.out.println("block() on event loop -> " + e.getMessage()));
  }

  @Test
  void subscribeInsteadOfBlock() throws InterruptedException {
    Flux<Recipe> recipes = client.findQuick(30);

    recipes.subscribe(
        recipe -> System.out.println("subscribe -> " + recipe.name()),
        error -> System.out.println("subscribe failed: " + error.getMessage()),
        () -> System.out.println("subscribe -> completed"));

    // the test thread continues; wait a moment so the output appears before the test ends
    StepVerifier.create(recipes).expectNextCount(2).verifyComplete();
  }
}
