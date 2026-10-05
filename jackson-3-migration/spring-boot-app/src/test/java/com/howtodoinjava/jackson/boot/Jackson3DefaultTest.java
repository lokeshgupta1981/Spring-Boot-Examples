package com.howtodoinjava.jackson.boot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

/** Spring Boot 4 auto-configures a Jackson 3 JsonMapper and uses it for Spring MVC. */
@SpringBootTest
@AutoConfigureMockMvc
class Jackson3DefaultTest {

  @Autowired
  JsonMapper jsonMapper;

  @Autowired
  MockMvcTester mvc;

  @Test
  void bootCreatesJackson3JsonMapper() {
    assertThat(jsonMapper.getClass().getName()).isEqualTo("tools.jackson.databind.json.JsonMapper");
    assertThat(jsonMapper.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)).isTrue();
    // set by our JsonMapperBuilderCustomizer bean
    assertThat(jsonMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)).isTrue();
  }

  @Test
  void responseUsesJackson3Defaults() throws Exception {
    String body = mvc.get().uri("/ingredients/flour").exchange().getResponse().getContentAsString();
    System.out.println("Jackson 3 (default): " + body);

    assertThat(body).isEqualTo("{\"addedOn\":\"2026-10-05\",\"grams\":250,\"name\":\"flour\"}");
  }

  @Test
  void customizerRejectsUnknownProperty() {
    assertThat(mvc.post().uri("/ingredients")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"flour\",\"grams\":250,\"color\":\"white\"}"))
        .hasStatus(400);
  }
}
