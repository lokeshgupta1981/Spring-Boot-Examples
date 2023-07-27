package com.howtodoinjava.app.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {

    Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
    Set<String> messages = new HashSet<>(constraintViolations.size());
    messages.addAll(constraintViolations.stream()
        .map(constraintViolation -> String.format("%s value '%s' %s",
            constraintViolation.getPropertyPath(),
            constraintViolation.getInvalidValue(), constraintViolation.getMessage()))
        .collect(Collectors.toList()));

    return new ResponseEntity<>("Your custom error message: " + messages, HttpStatus.BAD_REQUEST);
  }
}
