package com.howtodoinjava.demo.config;

import com.howtodoinjava.demo.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class LoggingPersonProcessor implements ItemProcessor<Person, Person> {
  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingPersonProcessor.class);

  @Override
  public Person process(Person item) throws Exception {
    LOGGER.info("Processing person information: {}", item);
    return item;
  }
}
