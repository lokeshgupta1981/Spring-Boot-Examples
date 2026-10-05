package com.howtodoinjava.jackson.v3;

import com.howtodoinjava.jackson.model.Recipe;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;

/** Jackson 3 style: immutable JsonMapper, unchecked JacksonException, java.time built in. */
public class RecipeJson3 {

  private final JsonMapper mapper = JsonMapper.builder().build();

  public Optional<Recipe> read(String json) {
    Optional<Recipe> recipe;
    try {
      recipe = Optional.of(mapper.readValue(json, Recipe.class));
    } catch (JacksonException e) {            // extends RuntimeException
      recipe = Optional.empty();
    }
    return recipe;
  }

  public JsonMapper mapper() {
    return mapper;
  }
}
