package com.howtodoinjava.demo.batch.jobs.listener;

import com.howtodoinjava.demo.batch.jobs.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.SkipListener;

public class BookSkipListener implements SkipListener<Book, Book> {

  private static final Logger log = LoggerFactory.getLogger(BookSkipListener.class);

  @Override
  public void onSkipInRead(Throwable t) {
    log.warn("Skipped a bad line: {}", t.getMessage());
  }
}
