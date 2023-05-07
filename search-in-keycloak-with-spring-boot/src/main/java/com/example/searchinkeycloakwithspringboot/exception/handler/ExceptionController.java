package com.example.searchinkeycloakwithspringboot.exception.handler;

import com.example.searchinkeycloakwithspringboot.exception.UserNotFoundInKeycloakException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

  @ExceptionHandler
  public ResponseEntity<String> handleUserNotFoundInKeycloakException(
      UserNotFoundInKeycloakException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }
}
