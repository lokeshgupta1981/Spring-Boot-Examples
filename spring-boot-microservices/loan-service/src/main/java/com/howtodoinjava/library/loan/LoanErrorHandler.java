package com.howtodoinjava.library.loan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

@RestControllerAdvice
public class LoanErrorHandler {

  private static final Logger log = LoggerFactory.getLogger(LoanErrorHandler.class);

  @ExceptionHandler(HttpClientErrorException.NotFound.class)
  ProblemDetail bookNotFound(HttpClientErrorException.NotFound e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Book not found");
  }

  // I/O error or timeout (after retries), 5xx from book-service, or circuit open
  @ExceptionHandler({ResourceAccessException.class, HttpServerErrorException.class,
      CallNotPermittedException.class})
  ProblemDetail bookServiceDown(Exception e) {
    log.warn("book-service unavailable: {}", NestedExceptionUtils.getMostSpecificCause(e).toString());
    return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
        "Book service is not available, try again later");
  }
}
