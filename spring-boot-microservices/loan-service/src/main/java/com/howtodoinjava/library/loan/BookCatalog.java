package com.howtodoinjava.library.loan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class BookCatalog {

  private static final Logger log = LoggerFactory.getLogger(BookCatalog.class);

  private final BookClient bookClient;

  public BookCatalog(BookClient bookClient) {
    this.bookClient = bookClient;
  }

  // Retry wraps CircuitBreaker: every attempt is recorded by the circuit breaker
  @Retry(name = "books")
  @CircuitBreaker(name = "books")
  public Book findBook(String id) {
    log.info("Calling book-service for book {}", id);
    return bookClient.getBook(id);
  }
}
