package com.howtodoinjava.library.book;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/books")
public class BookController {

  private static final Logger log = LoggerFactory.getLogger(BookController.class);

  // In-memory catalog; each service owns its own data
  private final Map<String, Book> books = Map.of(
      "1", new Book("1", "Dune", 2),
      "2", new Book("2", "Clean Code", 0));

  @GetMapping
  public Collection<Book> all() {
    return books.values();
  }

  @GetMapping("/{id}")
  public Book byId(@PathVariable String id) {
    Book book = books.get(id);
    if (book == null) {
      log.info("Book {} not found", id);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book " + id + " not found");
    }
    log.info("Found book {} with {} copies", book.title(), book.copies());
    return book;
  }
}
