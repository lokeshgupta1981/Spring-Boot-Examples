package com.howtodoinjava.embabel.domain;

import java.util.List;

public record Shelf(String genre, List<Book> books) {

  public Shelf {
    books = books == null ? List.of() : List.copyOf(books);
  }

  public boolean isEmpty() {
    return books.isEmpty();
  }
}
