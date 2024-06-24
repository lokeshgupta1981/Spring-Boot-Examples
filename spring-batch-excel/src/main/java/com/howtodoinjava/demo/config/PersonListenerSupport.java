package com.howtodoinjava.demo.config;

import com.howtodoinjava.demo.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.item.Chunk;

public class PersonListenerSupport extends ItemListenerSupport<Person, Person> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersonListenerSupport.class);

  @Override
  public void onReadError(Exception ex) {
    LOGGER.error("Encountered error on read : ", ex);
  }

  @Override
  public void onWriteError(Exception ex, Chunk<? extends Person> person) {
    LOGGER.error("Encountered error on write : ", person, ex);
  }

  @Override
  public void onProcessError(Person person, Exception ex) {
    LOGGER.error("Encountered error on process : ", person, ex);
  }
}
