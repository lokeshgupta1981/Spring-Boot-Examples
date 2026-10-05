package com.howtodoinjava.jackson;

import com.howtodoinjava.jackson.model.Recipe;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/** The intro snippet of the article. */
class QuickStartTest {

  @Test
  void jackson3InFourLines() {
    JsonMapper mapper = JsonMapper.builder().build();                    // immutable, java.time built in
    String json = mapper.writeValueAsString(new Recipe("pancakes", 4, LocalDate.of(2026, 10, 5)));
    Recipe recipe = mapper.readValue(json, Recipe.class);                // no checked exception

    System.out.println(json);
    assertThat(json).isEqualTo("{\"name\":\"pancakes\",\"servings\":4,\"createdOn\":\"2026-10-05\"}");
    assertThat(recipe.servings()).isEqualTo(4);
  }
}
