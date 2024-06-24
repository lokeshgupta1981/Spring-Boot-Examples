package com.howtodoinjava.demo.config;

import com.howtodoinjava.demo.model.Person;
import com.howtodoinjava.demo.model.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

public class DatabasePersonWriter implements ItemWriter<Person> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePersonWriter.class);

  @Autowired
  private PersonRepository personRepository;

  @Override
  public void write(Chunk<? extends Person> people) throws Exception {
    LOGGER.info("Writing to the Database the information of {} people", people.size());
    people.getItems().stream().forEach(personRepository::save);
  }
}
