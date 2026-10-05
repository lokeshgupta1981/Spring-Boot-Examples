package com.howtodoinjava.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("prod")
class ProdProfileTest {

  @Autowired
  MockMvcTester mvc;

  @Test
  void docsAreOffInProduction() {
    assertThat(mvc.get().uri("/v3/api-docs").exchange()).hasStatus(404);
    assertThat(mvc.get().uri("/swagger-ui.html").exchange()).hasStatus(404);
    assertThat(mvc.get().uri("/recipes").exchange()).hasStatusOk();
  }
}
