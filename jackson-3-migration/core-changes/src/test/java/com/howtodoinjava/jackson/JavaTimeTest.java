package com.howtodoinjava.jackson;

import com.howtodoinjava.jackson.model.Recipe;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** java.time and Optional support: module in Jackson 2, built in for Jackson 3. */
class JavaTimeTest {

  @Test
  void jackson2NeedsJavaTimeModule() {
    com.fasterxml.jackson.databind.ObjectMapper plain = new com.fasterxml.jackson.databind.ObjectMapper();

    assertThatThrownBy(() -> plain.writeValueAsString(LocalDate.of(2026, 10, 5)))
        .isInstanceOf(com.fasterxml.jackson.databind.exc.InvalidDefinitionException.class)
        .hasMessageContaining("Java 8 date/time type `java.time.LocalDate` not supported by default");
  }

  @Test
  void jackson3HasJavaTimeAndOptionalBuiltIn() {
    tools.jackson.databind.json.JsonMapper mapper = tools.jackson.databind.json.JsonMapper.builder().build();

    String date = mapper.writeValueAsString(LocalDate.of(2026, 10, 5));
    String fruit = mapper.writeValueAsString(Optional.of("apple"));
    Recipe recipe = mapper.readValue("{\"name\":\"pancakes\",\"servings\":4,\"createdOn\":\"2026-10-05\"}", Recipe.class);

    assertThat(date).isEqualTo("\"2026-10-05\"");
    assertThat(fruit).isEqualTo("\"apple\"");
    assertThat(recipe.createdOn()).isEqualTo(LocalDate.of(2026, 10, 5));
  }
}
