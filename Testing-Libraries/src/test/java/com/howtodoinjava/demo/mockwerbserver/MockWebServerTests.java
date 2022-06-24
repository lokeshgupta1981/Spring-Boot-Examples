package com.howtodoinjava.demo.mockwerbserver;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MockWebServerTests {
  public static MockWebServer server;
  final static Dispatcher dispatcher = new Dispatcher() {

    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {

      switch (request.getPath()) {
        case "/api-url-one":
          return new MockResponse()
              .setResponseCode(201);

        case "/api-url-two":
          return new MockResponse()
              .setHeader("x-header-name", "header-value")
              .setResponseCode(200)
              .setBody("<response />");

        case "/api-url-three":
          return new MockResponse()
              .setResponseCode(500)
              .setBodyDelay(5000, TimeUnit.MILLISECONDS)
              .setChunkedBody("<error-response />", 5);

        case "/api-url-four":
          return new MockResponse()
              .setResponseCode(200)
              .setBody("{\"data\":\"\"}")
              .throttleBody(1024, 5, TimeUnit.SECONDS);
      }
      return new MockResponse().setResponseCode(404);
    }
  };

  @BeforeAll
  static void setUp() throws IOException {
    server = new MockWebServer();
    server.setDispatcher(dispatcher);
    server.start(8080);
  }

  @AfterAll
  static void tearDown() throws IOException {
    server.shutdown();
  }

  @Test
  void simpleTest() throws InterruptedException {

    WebClient webClient = WebClient
        .create(String.format("http://%s:8080", server.getHostName()));

    Mono<String> apiResponse = webClient.post()
        .uri("/api-url-two")
        .body(Mono.just("<data />"), String.class)
        .header("Authorization", "Basic " +
            Base64Utils.encodeToString(("username:password").getBytes(UTF_8)))
        .retrieve()
        .bodyToMono(String.class);

    //Validate Response
    StepVerifier.create(apiResponse)
        .expectNext("<response />")
        .verifyComplete();

    //Verify API HIT
    RecordedRequest request = server.takeRequest();
    assertEquals("/api-url-two", request.getPath());
    assertEquals("POST", request.getMethod());
    assertNotNull(request.getHeader("Authorization"));
    assertEquals("<data />", request.getBody().readUtf8());
  }
}
