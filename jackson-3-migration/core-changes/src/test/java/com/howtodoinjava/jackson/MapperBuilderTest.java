package com.howtodoinjava.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.howtodoinjava.jackson.model.Ingredient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Jackson 2 mutable ObjectMapper vs Jackson 3 immutable JsonMapper built with a builder. */
class MapperBuilderTest {

  @Test
  void jackson2MapperIsConfiguredWithSetters() {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    assertThat(mapper.isEnabled(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT)).isTrue();
  }

  @Test
  void jackson3MapperIsBuiltOnce() {
    tools.jackson.databind.json.JsonMapper mapper = tools.jackson.databind.json.JsonMapper.builder()
        .enable(tools.jackson.databind.SerializationFeature.INDENT_OUTPUT)
        .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
        .build();

    assertThat(mapper.isEnabled(tools.jackson.databind.SerializationFeature.INDENT_OUTPUT)).isTrue();
    String json = mapper.writeValueAsString(Ingredient.of(null, 250, true));
    assertThat(json).doesNotContain("name");
  }

  @Test
  void jackson3RebuildCreatesANewMapper() {
    tools.jackson.databind.json.JsonMapper mapper = tools.jackson.databind.json.JsonMapper.builder()
        .enable(tools.jackson.databind.SerializationFeature.INDENT_OUTPUT)
        .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
        .build();

    tools.jackson.databind.json.JsonMapper compact = mapper.rebuild()
        .disable(tools.jackson.databind.SerializationFeature.INDENT_OUTPUT)
        .build();

    assertThat(mapper.isEnabled(tools.jackson.databind.SerializationFeature.INDENT_OUTPUT)).isTrue();
    assertThat(compact.isEnabled(tools.jackson.databind.SerializationFeature.INDENT_OUTPUT)).isFalse();
    assertThat(compact.writeValueAsString(Ingredient.of(null, 250, true))).doesNotContain("name");
    assertThat(compact).isNotSameAs(mapper);
  }
}
