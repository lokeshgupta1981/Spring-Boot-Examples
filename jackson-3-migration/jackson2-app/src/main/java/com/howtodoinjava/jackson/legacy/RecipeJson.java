package com.howtodoinjava.jackson.legacy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Optional;

public class RecipeJson {

  private final ObjectMapper mapper = new ObjectMapper();

  public RecipeJson() {
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  public Optional<Recipe> read(String json) {
    try {
      return Optional.of(mapper.readValue(json, Recipe.class));
    } catch (JsonProcessingException e) {
      return Optional.empty();
    }
  }

  public String write(Recipe recipe) throws JsonProcessingException {
    return mapper.writeValueAsString(recipe);
  }
}
