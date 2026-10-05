package com.howtodoinjava.embabel.library;

import com.howtodoinjava.embabel.domain.Book;
import com.howtodoinjava.embabel.domain.Shelf;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class LibraryCatalog {

  private final Map<String, List<Book>> booksByGenre = Map.of(
      "fantasy", List.of(new Book("The Hobbit", 310), new Book("Mistborn", 541)),
      "sci-fi", List.of(new Book("Dune", 412), new Book("Project Hail Mary", 476)),
      "mystery", List.of(new Book("The Hound of the Baskervilles", 256)));

  public Shelf shelfFor(String genre) {
    String key = genre == null ? "" : genre.strip().toLowerCase(Locale.ROOT);
    return new Shelf(key, booksByGenre.getOrDefault(key, List.of()));
  }
}
