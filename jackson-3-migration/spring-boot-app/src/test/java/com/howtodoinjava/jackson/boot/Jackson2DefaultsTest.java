package com.howtodoinjava.jackson.boot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

/** Jackson 3 with defaults aligned to Jackson 2: spring.jackson.use-jackson2-defaults=true. */
@SpringBootTest(properties = "spring.jackson.use-jackson2-defaults=true")
@AutoConfigureMockMvc
class Jackson2DefaultsTest {

  @Autowired
  MockMvcTester mvc;

  @Test
  void responseUsesJackson2LikeDefaults() throws Exception {
    String body = mvc.get().uri("/ingredients/flour").exchange().getResponse().getContentAsString();
    System.out.println("Jackson 3 + use-jackson2-defaults: " + body);

    assertThat(body).isEqualTo("{\"name\":\"flour\",\"grams\":250,\"addedOn\":\"2026-10-05\"}");
  }
}
