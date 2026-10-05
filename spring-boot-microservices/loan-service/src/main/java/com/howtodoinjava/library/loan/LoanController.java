package com.howtodoinjava.library.loan;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/loans")
public class LoanController {

  private static final Logger log = LoggerFactory.getLogger(LoanController.class);

  private final BookCatalog bookCatalog;
  private final Map<Long, Loan> loans = new ConcurrentHashMap<>();
  private final AtomicLong ids = new AtomicLong();

  public LoanController(BookCatalog bookCatalog) {
    this.bookCatalog = bookCatalog;
  }

  @GetMapping
  public Collection<Loan> all() {
    return loans.values();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Loan borrow(@RequestBody LoanRequest request) {
    Book book = bookCatalog.findBook(request.bookId());
    if (book.copies() < 1) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "No copies of " + book.title() + " left");
    }
    Loan loan = new Loan(ids.incrementAndGet(), book.id(), book.title(), request.member());
    loans.put(loan.id(), loan);
    log.info("Created loan {} for {}", loan.id(), loan.member());
    return loan;
  }
}
