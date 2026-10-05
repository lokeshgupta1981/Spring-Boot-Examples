package com.howtodoinjava.demo.model;

public class RecipeNotFoundException extends RuntimeException {

  public RecipeNotFoundException(Long id) {
    super("Recipe " + id + " not found");
  }
}
