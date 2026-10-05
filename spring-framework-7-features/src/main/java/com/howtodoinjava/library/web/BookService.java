package com.howtodoinjava.library.web;

import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class BookService {

  private final Map<String, Book> books = Map.of(
      "dune", new Book("dune", 3, "A3"),
      "emma", new Book("emma", 0, null));

  public @Nullable Book find(String title) {          // null for an unknown title
    return books.get(title);
  }
}
