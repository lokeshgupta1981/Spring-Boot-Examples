package com.howtodoinjava.library.book;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(BookController.class)
class BookControllerTest {

  @Autowired
  MockMvcTester mvc;

  @Test
  void returnsBookById() {
    assertThat(mvc.get().uri("/books/1"))
        .hasStatusOk()
        .bodyJson()
        .isLenientlyEqualTo("""
            {"id":"1","title":"Dune","copies":2}
            """);
  }

  @Test
  void returns404ForUnknownBook() {
    assertThat(mvc.get().uri("/books/9"))
        .hasStatus(HttpStatus.NOT_FOUND);
  }
}
