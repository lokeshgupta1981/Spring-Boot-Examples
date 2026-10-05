package com.howtodoinjava.library.loan;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.tracing.test.autoconfigure.AutoConfigureTracing;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@SpringBootTest(properties = "spring.http.serviceclient.books.read-timeout=500ms")
@AutoConfigureMockMvc
@AutoConfigureTracing
class LoanServiceTest {

  static final WireMockServer bookService = new WireMockServer(
      wireMockConfig().dynamicPort().http2PlainDisabled(true));

  static {
    bookService.start();
  }

  @DynamicPropertySource
  static void bookServiceUrl(DynamicPropertyRegistry registry) {
    registry.add("spring.http.serviceclient.books.base-url", bookService::baseUrl);
  }

  @AfterAll
  static void stop() {
    bookService.stop();
  }

  @Autowired
  MockMvcTester mvc;

  @Autowired
  CircuitBreakerRegistry circuitBreakers;

  @BeforeEach
  void reset() {
    bookService.resetAll();                                   // stubs and request log
    circuitBreakers.circuitBreaker("books").reset();          // circuit state is shared
  }

  @Test
  void createsLoanAndPropagatesTraceContext() {
    bookService.stubFor(get("/books/1")
        .willReturn(okJson("""
            {"id":"1","title":"Dune","copies":2}
            """)));

    assertThat(borrow("1"))
        .hasStatus(HttpStatus.CREATED)
        .bodyJson()
        .isLenientlyEqualTo("""
            {"bookId":"1","title":"Dune","member":"Lokesh"}
            """);

    // W3C trace context header: 00-<trace id>-<span id>-<flags>
    bookService.verify(getRequestedFor(urlEqualTo("/books/1"))
        .withHeader("traceparent", matching("00-[0-9a-f]{32}-[0-9a-f]{16}-[0-9a-f]{2}")));
  }

  @Test
  void rejectsLoanWhenNoCopiesLeft() {
    bookService.stubFor(get("/books/2")
        .willReturn(okJson("""
            {"id":"2","title":"Clean Code","copies":0}
            """)));

    assertThat(borrow("2"))
        .hasStatus(HttpStatus.CONFLICT);
  }

  @Test
  void unknownBookIsNotRetried() {
    bookService.stubFor(get("/books/9").willReturn(aResponse().withStatus(404)));

    assertThat(borrow("9"))
        .hasStatus(HttpStatus.NOT_FOUND);

    bookService.verify(1, getRequestedFor(urlEqualTo("/books/9")));
  }

  @Test
  void retriesThreeTimesThenOpensCircuit() {
    bookService.stubFor(get("/books/1")
        .willReturn(aResponse().withStatus(503)));

    // 1. First request: 3 attempts, 3 failed calls
    assertThat(borrow("1")).hasStatus(HttpStatus.SERVICE_UNAVAILABLE);
    bookService.verify(3, getRequestedFor(urlEqualTo("/books/1")));

    // 2. Second request: the 4th failed call opens the circuit,
    //    so the 2nd attempt gets CallNotPermittedException
    assertThat(borrow("1")).hasStatus(HttpStatus.SERVICE_UNAVAILABLE);
    bookService.verify(4, getRequestedFor(urlEqualTo("/books/1")));
    assertThat(circuitBreakers.circuitBreaker("books").getState())
        .isEqualTo(CircuitBreaker.State.OPEN);

    // 3. Circuit is open, so book-service is not called again
    assertThat(borrow("1")).hasStatus(HttpStatus.SERVICE_UNAVAILABLE);
    bookService.verify(4, getRequestedFor(urlEqualTo("/books/1")));
  }

  @Test
  void slowBookServiceTimesOut() {
    bookService.stubFor(get("/books/1")
        .willReturn(okJson("""
            {"id":"1","title":"Dune","copies":2}
            """).withFixedDelay(2_000)));

    long start = System.currentTimeMillis();
    assertThat(borrow("1")).hasStatus(HttpStatus.SERVICE_UNAVAILABLE);
    long took = System.currentTimeMillis() - start;

    // 3 attempts x 500 ms read timeout + 2 x 200 ms wait, well below 3 x 2 s
    assertThat(took).isLessThan(4_000);
  }

  private MockMvcTester.MockMvcRequestBuilder borrow(String bookId) {
    return mvc.post().uri("/loans")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"bookId":"%s","member":"Lokesh"}
            """.formatted(bookId));
  }
}
