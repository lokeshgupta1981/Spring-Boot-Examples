package com.howtodoinjava.app.service.impl;

import com.howtodoinjava.app.model.Item;
import com.howtodoinjava.app.service.MongoTemplateService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;

import java.util.List;

@SpringBootTest
class MongoTemplateServiceImplTest {

  @Autowired
  MongoTemplateService mongoTemplateService;

  Integer pageNumber = 1;
  Integer pageSize = 5;
  Item item1 = null;
  Item item2 = null;
  Item item1ToUpdate = null;

  String searchCategory = "snacks";

  @BeforeEach
  void setUp() {
    item1 = new Item();
    item1.setId(3);
    item1.setName("Dried Red Chilli");
    item1.setQuantity(3);
    item1.setCategory("spices");

    item1ToUpdate = new Item();
    item1ToUpdate.setId(3);
    item1ToUpdate.setQuantity(1);
    item1ToUpdate.setCategory("spices");
    item1ToUpdate.setName("Dried Red Chilli");

    item2 = new Item();
    item2.setId(4);
    item2.setName("Cheese Crackers");
    item2.setQuantity(8);
    item2.setCategory("snacks");

  }

  @AfterEach
  void tearDown() {
    item1 = null;
    item1ToUpdate = null;
    item2 = null;
  }


  @Test
  void addItem() {

    Item savedItem = mongoTemplateService.add(item1);

    Assert.assertEquals(savedItem.getId(), item1.getId());
    Assert.assertEquals(savedItem.getName(), item1.getName());
    Assert.assertEquals(savedItem.getQuantity(), item1.getQuantity());
    Assert.assertEquals(savedItem.getCategory(), item1.getCategory());

    mongoTemplateService.delete(savedItem.getId());
  }

  @Test
  void updateItem() {
    mongoTemplateService.add(item1);

    Item updatedItem = mongoTemplateService.update(item1ToUpdate);

    Assert.assertEquals(updatedItem.getQuantity(),
            item1ToUpdate.getQuantity());

    mongoTemplateService.delete(updatedItem.getId());
  }

  @Test
  void getAllItem() {
    mongoTemplateService.add(item1);
    mongoTemplateService.add(item2);

    List<Item> items = mongoTemplateService.getAll();
    Assert.assertTrue(items.size() == 2);

    mongoTemplateService.delete(item1.getId());
    mongoTemplateService.delete(item2.getId());

  }

  @Test
  void getItemById() {
    mongoTemplateService.add(item1);

    Item item = mongoTemplateService.getById(3);

    Assert.assertEquals(item.getName(), item.getName());

  }

  @Test
  void searchItemWithName() {

    mongoTemplateService.add(item1);
    mongoTemplateService.add(item2);


    List<Item> items = mongoTemplateService.search("Dried Red " +
            "Chilli", null,
        null, null);

    Assert.assertTrue(items.size() > 0);

    mongoTemplateService.delete(item1.getId());
    mongoTemplateService.delete(item2.getId());
  }

  @Test
  void searchItemWithQuantity() {
    mongoTemplateService.add(item1);
    mongoTemplateService.add(item2);

    List<Item> items = mongoTemplateService.search(null, 1,
        5, null);

    Assert.assertTrue(items.size() > 0);

    mongoTemplateService.delete(item1.getId());
    mongoTemplateService.delete(item2.getId());

  }

  @Test
  void searchItemWithCategory() {
    mongoTemplateService.add(item1);
    mongoTemplateService.add(item2);

    List<Item> items = mongoTemplateService.search(null, null,
        null, "spices");

    Assert.assertTrue(items.size() > 0);

    mongoTemplateService.delete(item1.getId());
    mongoTemplateService.delete(item2.getId());

  }

  @Test
  void searchItemWithAll() {
    mongoTemplateService.add(item1);
    mongoTemplateService.add(item2);

    List<Item> items = mongoTemplateService.search("Dried Red " +
            "Chilli", 1,
        5, "spices");

    Assert.assertTrue(items.size() > 0);

    mongoTemplateService.delete(item1.getId());
    mongoTemplateService.delete(item2.getId());

  }

  @Test
  void findAllWithPagination() {
    mongoTemplateService.add(item1);
    mongoTemplateService.add(item2);

    List<Item> items = mongoTemplateService.getAll(1, 5);

    Assert.assertTrue(items.size() > 0);

    mongoTemplateService.delete(item1.getId());
    mongoTemplateService.delete(item2.getId());

  }

  @Test
  void deleteItemById() {
    mongoTemplateService.add(item1);

    boolean result = mongoTemplateService.delete(item1.getId());

    Assert.assertEquals(result, true);

    mongoTemplateService.delete(item1.getId());
    mongoTemplateService.delete(item2.getId());

  }
}