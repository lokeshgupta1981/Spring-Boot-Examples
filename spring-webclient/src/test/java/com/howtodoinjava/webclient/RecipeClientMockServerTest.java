package com.howtodoinjava.webclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import reactor.core.publisher.Mono;

/**
 * Tests RecipeClient without the Spring context. MockWebServer plays the remote API,
 * so we can return any status or delay we like.
 */
class RecipeClientMockServerTest {

  MockWebServer server;
  RecipeClient client;

  @BeforeEach
  void startServer() throws IOException {
    server = new MockWebServer();
    server.start();
    client = new RecipeClient(WebClient.builder(), server.url("/").toString());
  }

  @AfterEach
  void stopServer() throws IOException {
    server.shutdown();
  }

  @Test
  void parsesJsonBodyAndSendsHeaders() throws InterruptedException {
    server.enqueue(new MockResponse()
        .setHeader("Content-Type", "application/json")
        .setBody("{\"id\":7,\"name\":\"Curry\",\"minutes\":45}"));

    Recipe recipe = client.findById(7).block();
    RecordedRequest request = server.takeRequest();
    System.out.println("mock server got " + request.getMethod() + " " + request.getPath()
        + " User-Agent=" + request.getHeader("User-Agent"));
    System.out.println("client parsed " + recipe);

    assertThat(request.getPath()).isEqualTo("/recipes/7");
    assertThat(recipe.minutes()).isEqualTo(45);
  }

  @Test
  void serverErrorBecomesWebClientResponseException() {
    server.enqueue(new MockResponse().setResponseCode(500).setBody("boom"));

    assertThatThrownBy(() -> client.findById(1).block())
        .isInstanceOf(WebClientResponseException.InternalServerError.class)
        .hasMessageContaining("500 Internal Server Error from GET");
    System.out.println("500 from server -> WebClientResponseException$InternalServerError");
  }

  @Test
  void timeoutOperatorGivesUpOnSlowResponse() {
    server.enqueue(new MockResponse()
        .setHeader("Content-Type", "application/json")
        .setBody("{\"id\":1,\"name\":\"Pancakes\",\"minutes\":20}")
        .setBodyDelay(3, java.util.concurrent.TimeUnit.SECONDS));

    Mono<Recipe> slow = client.findById(1).timeout(Duration.ofSeconds(1));

    assertThatThrownBy(slow::block)
        .satisfies(e -> System.out.println("timeout(1s) -> " + e.getClass().getName() + ": " + e.getMessage()));
  }

  @Test
  void timeoutOperatorWithFallback() {
    server.enqueue(new MockResponse()
        .setHeader("Content-Type", "application/json")
        .setBody("{\"id\":1,\"name\":\"Pancakes\",\"minutes\":20}")
        .setBodyDelay(3, java.util.concurrent.TimeUnit.SECONDS));
    Recipe fallback = new Recipe(0L, "none", 0);

    Recipe recipe = client.findById(1)
        .timeout(Duration.ofSeconds(1))
        .onErrorReturn(java.util.concurrent.TimeoutException.class, fallback)
        .block();
    System.out.println("timeout with fallback -> " + recipe);

    assertThat(recipe).isEqualTo(fallback);
  }
}
