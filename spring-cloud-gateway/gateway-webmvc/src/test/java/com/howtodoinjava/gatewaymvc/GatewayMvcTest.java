package com.howtodoinjava.gatewaymvc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

import com.github.tomakehurst.wiremock.WireMockServer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayMvcTest {

  static final WireMockServer recipeService = new WireMockServer(wireMockConfig().dynamicPort());

  static {
    recipeService.start();
  }

  @DynamicPropertySource
  static void recipeServiceUri(DynamicPropertyRegistry registry) {
    registry.add("recipe-service.uri", recipeService::baseUrl);
  }

  @AfterAll
  static void stopWireMock() {
    recipeService.stop();
  }

  @LocalServerPort
  int port;

  @Test
  void stripsPrefixAndAddsHeaders() {
    recipeService.stubFor(get("/recipes/omelette").willReturn(aResponse()
        .withHeader("Content-Type", "application/json")
        .withBody("{\"name\":\"omelette\",\"minutes\":10}")));

    ResponseEntity<String> response = RestClient.create("http://localhost:" + port)
        .get().uri("/api/recipes/omelette")
        .retrieve()
        .toEntity(String.class);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getHeaders().getFirst("X-Served-By")).isEqualTo("recipe-gateway-mvc");
    assertThat(response.getBody()).contains("\"minutes\":10");
    recipeService.verify(getRequestedFor(urlEqualTo("/recipes/omelette"))
        .withHeader("X-Request-Source", equalTo("recipe-gateway-mvc")));
  }
}
