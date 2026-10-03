package com.howtodoinjava.demo.batch.jobs.csvToDb.listener;

import com.howtodoinjava.demo.batch.jobs.csvToDb.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;

public class PersonItemProcessListener implements ItemProcessListener<Person, Person> {

  public static final Logger logger = LoggerFactory.getLogger(PersonItemProcessListener.class);

  @Override
  public void beforeProcess(Person input) {
    logger.info("Person record has been read: " + input);
  }

  @Override
  public void afterProcess(Person input, Person result) {
    logger.info("Person record has been processed to : " + result);
  }

  @Override
  public void onProcessError(Person input, Exception e) {
    logger.error("Error in reading the person record : " + input);
    logger.error("Error in reading the person record : " + e);
  }
}
