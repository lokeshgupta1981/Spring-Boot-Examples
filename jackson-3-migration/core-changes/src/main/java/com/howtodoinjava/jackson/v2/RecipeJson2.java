package com.howtodoinjava.jackson.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.howtodoinjava.jackson.model.Recipe;

import java.util.Optional;

/** Jackson 2 style: mutable mapper, checked JsonProcessingException, JavaTimeModule registered. */
public class RecipeJson2 {

  private final ObjectMapper mapper = new ObjectMapper();

  public RecipeJson2() {
    mapper.registerModule(new JavaTimeModule());
  }

  public Optional<Recipe> read(String json) {
    Optional<Recipe> recipe;
    try {
      recipe = Optional.of(mapper.readValue(json, Recipe.class));
    } catch (JsonProcessingException e) {     // extends IOException
      recipe = Optional.empty();
    }
    return recipe;
  }

  public ObjectMapper mapper() {
    return mapper;
  }

  /** The recommended Jackson 2 style that is closest to Jackson 3. */
  public static JsonMapper builtMapper() {
    return JsonMapper.builder()
        .addModule(new JavaTimeModule())
        .build();
  }
}
