package com.howtodoinjava.jackson.boot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

/** Deprecated Jackson 2 support: spring-boot-jackson2 plus preferred-json-mapper=jackson2. */
@SpringBootTest(properties = "spring.http.converters.preferred-json-mapper=jackson2")
@AutoConfigureMockMvc
class Jackson2FallbackTest {

  @Autowired
  com.fasterxml.jackson.databind.ObjectMapper jackson2Mapper;

  @Autowired
  MockMvcTester mvc;

  @Test
  void springMvcUsesJackson2ObjectMapper() throws Exception {
    String body = mvc.get().uri("/ingredients/flour").exchange().getResponse().getContentAsString();
    System.out.println("Jackson 2 (preferred-json-mapper=jackson2): " + body);

    assertThat(jackson2Mapper).isNotNull();
    assertThat(body).isEqualTo("{\"name\":\"flour\",\"grams\":250,\"addedOn\":\"2026-10-05\"}");
  }
}
