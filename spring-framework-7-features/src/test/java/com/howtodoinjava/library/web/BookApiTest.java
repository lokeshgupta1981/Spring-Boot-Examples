package com.howtodoinjava.library.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BookApiTest {

  @Autowired
  WebApplicationContext context;

  @Autowired
  BookService bookService;

  RestTestClient client;

  @BeforeEach
  void setUp() {
    client = RestTestClient.bindToApplicationContext(context).build();
  }

  @Test
  void noVersionUsesDefaultVersionOne() {
    String body = client.get().uri("/books/dune").exchange()
        .expectStatus().isOk()
        .expectBody(String.class).returnResult().getResponseBody();
    System.out.println("GET /books/dune -> " + body);
    assertThat(body).isEqualTo("{\"title\":\"dune\",\"copies\":3}");
  }

  @Test
  void versionTwoAddsShelf() {
    client.get().uri("/books/dune?version=2").exchange()
        .expectStatus().isOk()
        .expectBody().jsonPath("$.shelf").isEqualTo("A3");

    String body = client.get().uri("/books/dune?version=2").exchange()
        .expectBody(String.class).returnResult().getResponseBody();
    System.out.println("GET /books/dune?version=2 -> " + body);
    assertThat(body).isEqualTo("{\"title\":\"dune\",\"copies\":3,\"shelf\":\"A3\"}");
  }

  @Test
  void jackson3MapperSkipsNullShelf() {
    String body = client.get().uri("/books/emma?version=2").exchange()
        .expectStatus().isOk()
        .expectBody(String.class).returnResult().getResponseBody();
    System.out.println("GET /books/emma?version=2 -> " + body);
    assertThat(body).isEqualTo("{\"title\":\"emma\",\"copies\":0}");
  }

  @Test
  void unsupportedVersionIsBadRequest() {
    String body = client.get().uri("/books/dune?version=3").exchange()
        .expectStatus().isBadRequest()
        .expectBody(String.class).returnResult().getResponseBody();
    System.out.println("GET /books/dune?version=3 -> 400 " + body);
  }

  @Test
  void unknownBookIsNotFound() {
    client.get().uri("/books/unknown").exchange()
        .expectStatus().isNotFound();
  }

  @Test
  void findReturnsNullForUnknownTitle() {
    Book book = bookService.find("unknown");
    assertThat(book).isNull();
  }
}
