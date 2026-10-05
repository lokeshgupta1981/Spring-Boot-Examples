package com.howtodoinjava.library.gateway;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.github.tomakehurst.wiremock.WireMockServer;

@SpringBootTest
@AutoConfigureMockMvc
class GatewayRoutesTest {

  // One WireMock server stands in for both services
  static final WireMockServer backend = new WireMockServer(
      wireMockConfig().dynamicPort().http2PlainDisabled(true));

  static {
    backend.start();
  }

  @DynamicPropertySource
  static void serviceUrls(DynamicPropertyRegistry registry) {
    registry.add("library.book-service", backend::baseUrl);
    registry.add("library.loan-service", backend::baseUrl);
  }

  @AfterAll
  static void stop() {
    backend.stop();
  }

  @Autowired
  MockMvcTester mvc;

  @Test
  void routesBookRequestsWithoutApiPrefix() {
    backend.stubFor(get("/books/1").willReturn(okJson("""
        {"id":"1","title":"Dune","copies":2}
        """)));

    assertThat(mvc.get().uri("/api/books/1"))
        .hasStatusOk()
        .bodyJson()
        .isLenientlyEqualTo("""
            {"id":"1","title":"Dune","copies":2}
            """);
    backend.verify(getRequestedFor(urlEqualTo("/books/1")));
  }

  @Test
  void routesLoanPostWithBody() {
    backend.stubFor(post("/loans").willReturn(okJson("""
        {"id":1,"bookId":"1","title":"Dune","member":"Lokesh"}
        """).withStatus(201)));

    assertThat(mvc.post().uri("/api/loans")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"bookId":"1","member":"Lokesh"}
            """))
        .hasStatus(HttpStatus.CREATED);
    backend.verify(postRequestedFor(urlEqualTo("/loans"))
        .withRequestBody(equalToJson("""
            {"bookId":"1","member":"Lokesh"}
            """)));
  }

  @Test
  void unknownPathIsNotRouted() {
    assertThat(mvc.get().uri("/api/members/1"))
        .hasStatus(HttpStatus.NOT_FOUND);
  }
}
