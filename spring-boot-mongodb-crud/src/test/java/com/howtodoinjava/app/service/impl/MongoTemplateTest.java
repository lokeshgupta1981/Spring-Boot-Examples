package com.howtodoinjava.app.service.impl;

import com.howtodoinjava.app.model.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest
public class MongoTemplateTest {

  @Autowired
  MongoTemplate mongoTemplate;

  @Test
  void testCreateItem(){
    //Test
    Item item = new Item(null, "Red Chili", 3, "Spices");
    Item savedItem = mongoTemplate.save(item);

    //Validate
    Assertions.assertNotNull(savedItem.getId());

    //Clean
    mongoTemplate.remove(savedItem);
  }
}
