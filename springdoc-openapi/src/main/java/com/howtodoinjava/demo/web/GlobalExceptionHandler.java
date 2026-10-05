package com.howtodoinjava.demo.web;

import com.howtodoinjava.demo.model.RecipeNotFoundException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(RecipeNotFoundException.class)
  public Map<String, String> notFound(RecipeNotFoundException e) {
    return Map.of("error", e.getMessage());
  }
}
