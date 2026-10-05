package com.howtodoinjava.gateway;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.github.tomakehurst.wiremock.WireMockServer;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "10s")
class GatewayRoutesTest {

  @Container
  @ServiceConnection(name = "redis")
  static final GenericContainer<?> redis = new GenericContainer<>("redis:8.10.2").withExposedPorts(6379);

  // WireMock plays the recipe service on the port that application.yml routes to
  static final WireMockServer recipeService = new WireMockServer(wireMockConfig().port(8081));

  static {
    recipeService.start();
  }

  @AfterAll
  static void stopWireMock() {
    recipeService.stop();
  }

  @Autowired
  WebTestClient client;

  @BeforeEach
  void resetStubs() {
    recipeService.resetAll();
  }

  @Test
  void rewritesPathAndAddsHeaders() {
    recipeService.stubFor(get("/recipes/pancakes")
        .willReturn(okJson("{\"name\":\"pancakes\",\"minutes\":20}")));

    client.get().uri("/api/recipes/pancakes")
        .header("X-User", "headers-test")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().valueEquals("X-Served-By", "recipe-gateway")
        .expectBody().jsonPath("$.minutes").isEqualTo(20);

    recipeService.verify(getRequestedFor(urlEqualTo("/recipes/pancakes"))
        .withHeader("X-Request-Source", equalTo("recipe-gateway")));
  }

  @Test
  void rejectsRequestsOverTheRateLimit() {
    recipeService.stubFor(get("/recipes/pancakes")
        .willReturn(okJson("{\"name\":\"pancakes\",\"minutes\":20}")));

    List<Integer> statuses = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      int status = client.get().uri("/api/recipes/pancakes")
          .header("X-User", "rate-limit-test")
          .exchange()
          .returnResult(String.class)
          .getStatus().value();
      statuses.add(status);
    }

    assertThat(statuses.subList(0, 2)).containsOnly(200);   // burstCapacity = 2
    assertThat(statuses).contains(429);                    // Too Many Requests
  }

  @Test
  void retriesOn503UntilTheServiceAnswers() {
    recipeService.stubFor(get("/flaky/recipes").inScenario("flaky")
        .whenScenarioStateIs(STARTED).willReturn(aResponse().withStatus(503))
        .willSetStateTo("second"));
    recipeService.stubFor(get("/flaky/recipes").inScenario("flaky")
        .whenScenarioStateIs("second").willReturn(aResponse().withStatus(503))
        .willSetStateTo("third"));
    recipeService.stubFor(get("/flaky/recipes").inScenario("flaky")
        .whenScenarioStateIs("third").willReturn(okJson("{\"name\":\"pancakes\"}")));

    client.get().uri("/api/flaky/recipes")
        .exchange()
        .expectStatus().isOk();

    recipeService.verify(3, getRequestedFor(urlEqualTo("/flaky/recipes")));
  }

  @Test
  void returnsFallbackWhenServiceIsTooSlow() {
    recipeService.stubFor(get("/slow/recipes")
        .willReturn(okJson("{\"name\":\"lasagna\"}").withFixedDelay(3000)));

    client.get().uri("/api/slow/recipes")
        .exchange()
        .expectStatus().isEqualTo(503)
        .expectBody().jsonPath("$.message").isEqualTo("Recipe service is slow, try again later");
  }

  @Test
  void answersCorsPreflightForAllowedOrigin() {
    client.options().uri("/api/recipes/pancakes")
        .header("Origin", "http://localhost:3000")
        .header("Access-Control-Request-Method", "GET")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().valueEquals("Access-Control-Allow-Origin", "http://localhost:3000");

    client.options().uri("/api/recipes/pancakes")
        .header("Origin", "http://evil.example")
        .header("Access-Control-Request-Method", "GET")
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void listsRoutesOnActuatorEndpoint() {
    client.get().uri("/actuator/gateway/routes")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$[*].route_id").value(ids ->
            assertThat(ids.toString()).contains("recipes", "flaky-recipes", "slow-recipes"));
  }
}
