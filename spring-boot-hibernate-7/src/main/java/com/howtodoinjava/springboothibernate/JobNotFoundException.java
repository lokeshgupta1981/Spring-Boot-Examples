package com.howtodoinjava.springboothibernate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class JobNotFoundException extends RuntimeException {

  public JobNotFoundException(String what) {
    super(what + " not found");
  }
}
