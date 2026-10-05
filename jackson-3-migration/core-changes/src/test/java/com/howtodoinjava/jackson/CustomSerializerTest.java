package com.howtodoinjava.jackson;

import com.howtodoinjava.jackson.model.Grams;
import com.howtodoinjava.jackson.v2.GramsSerializer2;
import com.howtodoinjava.jackson.v3.GramsSerializer3;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** JsonSerializer + SimpleModule (Jackson 2) vs ValueSerializer + SimpleModule (Jackson 3). */
class CustomSerializerTest {

  @Test
  void jackson2Serializer() throws Exception {
    com.fasterxml.jackson.databind.module.SimpleModule module = new com.fasterxml.jackson.databind.module.SimpleModule();
    module.addSerializer(Grams.class, new GramsSerializer2());
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.registerModule(module);

    String json = mapper.writeValueAsString(Map.of("flour", new Grams(250)));
    assertThat(json).isEqualTo("{\"flour\":\"250 g\"}");
  }

  @Test
  void jackson3Serializer() {
    tools.jackson.databind.module.SimpleModule module = new tools.jackson.databind.module.SimpleModule();
    module.addSerializer(Grams.class, new GramsSerializer3());
    tools.jackson.databind.json.JsonMapper mapper = tools.jackson.databind.json.JsonMapper.builder()
        .addModule(module)
        .build();

    String json = mapper.writeValueAsString(Map.of("flour", new Grams(250)));
    assertThat(json).isEqualTo("{\"flour\":\"250 g\"}");
  }
}
