package com.howtodoinjava.boot4;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PantryNotFoundException extends RuntimeException {

  public PantryNotFoundException(String name) {
    super("No pantry item: " + name);
  }
}
