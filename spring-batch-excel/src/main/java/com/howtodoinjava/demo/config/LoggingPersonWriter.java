package com.howtodoinjava.demo.config;

import com.howtodoinjava.demo.model.Person;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingPersonWriter implements ItemWriter<Person> {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingPersonWriter.class);

  @Override
  public void write(Chunk<? extends Person> items) throws Exception {
    LOGGER.info("Received the information of {} people", items.size());
    items.forEach(i -> LOGGER.debug("Received the information of a person: {}", i));
  }
}
