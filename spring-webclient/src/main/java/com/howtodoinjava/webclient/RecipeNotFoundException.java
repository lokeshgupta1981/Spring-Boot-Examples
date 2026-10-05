package com.howtodoinjava.webclient;

public class RecipeNotFoundException extends RuntimeException {

  public RecipeNotFoundException(long id) {
    super("Recipe " + id + " not found");
  }
}
