package com.howtodoinjava.demo.batch.jobs.processor;

import com.howtodoinjava.demo.batch.jobs.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class BookItemProcessor implements ItemProcessor<Book, Book> {

  private static final Logger log = LoggerFactory.getLogger(BookItemProcessor.class);

  @Override
  public Book process(Book book) {
    if (book.author() == null || book.author().isBlank()) {
      log.info("Filtered out (no author): {}", book.title());
      return null;   // null means: do not write this item
    }
    return new Book(book.title().trim().toUpperCase(), book.author().trim(),
        book.pages(), book.price());
  }
}
