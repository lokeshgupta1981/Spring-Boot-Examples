package com.howtodoinjava.library.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

  private final BookService bookService;

  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @GetMapping(path = "/books/{title}", version = "1")
  public ResponseEntity<BookSummary> bookV1(@PathVariable String title) {
    Book book = bookService.find(title);
    if (book == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(new BookSummary(book.title(), book.copies()));
  }

  @GetMapping(path = "/books/{title}", version = "2")
  public ResponseEntity<Book> bookV2(@PathVariable String title) {
    Book book = bookService.find(title);
    return book == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(book);
  }
}
